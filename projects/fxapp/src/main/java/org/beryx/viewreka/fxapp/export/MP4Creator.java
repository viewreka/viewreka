/**
 * Inspired by (and using code from) jcodec samples (https://github.com/jcodec/jcodec)
 */
package org.beryx.viewreka.fxapp.export;

import static org.jcodec.common.NIOUtils.writableFileChannel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;

import javax.imageio.ImageIO;

import org.beryx.viewreka.core.ViewrekaException;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.RgbToYuv420p;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MP4Creator {
	private static final Logger log = LoggerFactory.getLogger(MP4Creator.class);

    private int timescale = 25;

    public int getTimescale() {
		return timescale;
	}
    public void setTimescale(int timescale) {
		this.timescale = timescale;
	}

    public void imagesToMP4(AtomicBoolean cancelFlag, File outputFile, IntFunction<BufferedImage> imageProvider, int imageCount, long imageDurationMillis) throws IOException {
        try (SeekableByteChannel sink = writableFileChannel(outputFile)) {
            outputFile.delete();
            MP4Muxer muxer = new MP4Muxer(sink, Brand.MP4);

            FramesMP4MuxerTrack outTrack = muxer.addTrack(TrackType.VIDEO, timescale);

            H264Encoder encoder = new H264Encoder();

            RgbToYuv420p transform = new RgbToYuv420p(0, 0);

            ArrayList<ByteBuffer> spsList = new ArrayList<>();
            ArrayList<ByteBuffer> ppsList = new ArrayList<>();
            for (int i = 0; i < imageCount; i++) {
            	if(cancelFlag.get()) break;
            	BufferedImage rgb = imageProvider.apply(i);
                Picture yuv = Picture.create(rgb.getWidth(), rgb.getHeight(), encoder.getSupportedColorSpaces()[0]);
                transform.transform(fromBufferedImage(rgb), yuv);
                ByteBuffer buf = ByteBuffer.allocate(rgb.getWidth() * rgb.getHeight() * 3);

                ByteBuffer result = encoder.encodeFrame(yuv, buf);

                spsList.clear();
                ppsList.clear();
                H264Utils.wipePS(result, spsList, ppsList);
                H264Utils.encodeMOVPacket(result);

                long framesPerImage = (imageDurationMillis * timescale + 500) / 1000;
                if(framesPerImage <= 0) framesPerImage = 1;
                for(int k=0; k<framesPerImage; k++) {
                    log.debug("{}: {}", i, k);
                    long frameNo = framesPerImage * i + k;
					 MP4Packet mp4Packet = new MP4Packet(result, frameNo, timescale, 1, frameNo, true, null, frameNo, 0);
                    mp4Packet.setData(result);
                    outTrack.addFrame(mp4Packet);
                }
            }
            outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList, 4));
            muxer.writeHeader();
        }
    }

    public void imageFilesToMP4(AtomicBoolean cancelFlag, File outputFile, IntFunction<String> imageNameProvider, int imageCount, long imageDurationMillis) throws IOException {
    	IntFunction<BufferedImage> imageProvider = i -> {
            File nextImg = new File(imageNameProvider.apply(i));
            if (!nextImg.exists()) throw new ViewrekaException("File '" + nextImg.getAbsolutePath() + "' not found.");
			try {
				return ImageIO.read(nextImg);
			} catch(Exception e) {
				throw new ViewrekaException("Error retrieving image from '" + nextImg.getAbsolutePath() + "'.", e);
			}
    	};
    	imagesToMP4(cancelFlag, outputFile, imageProvider, imageCount, imageDurationMillis);
    }

    public static Picture fromBufferedImage(BufferedImage src) {
        Picture dst = Picture.create(src.getWidth(), src.getHeight(), ColorSpace.RGB);
        fromBufferedImage(src, dst);
        return dst;
    }

    public static void fromBufferedImage(BufferedImage src, Picture dst) {
        int[] dstData = dst.getPlaneData(0);
        int off = 0;
        for (int i = 0; i < src.getHeight(); i++) {
            for (int j = 0; j < src.getWidth(); j++) {
                int rgb1 = src.getRGB(j, i);
                dstData[off++] = (rgb1 >> 16) & 0xff;
                dstData[off++] = (rgb1 >> 8) & 0xff;
                dstData[off++] = rgb1 & 0xff;
            }
        }
    }
}
