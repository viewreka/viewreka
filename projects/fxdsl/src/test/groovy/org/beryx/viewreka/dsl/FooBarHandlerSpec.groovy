package org.beryx.viewreka.dsl

import org.beryx.viewreka.core.ViewrekaException
import org.beryx.viewreka.dsl.foobar.BazHandler
import org.beryx.viewreka.dsl.foobar.FooBarHandler
import org.beryx.viewreka.dsl.helper.KeywordParser
import org.beryx.viewreka.dsl.transform.KeywordVisitor
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import spock.lang.Specification

class FooBarHandlerSpec extends Specification {
    def parser = new KeywordParser('/foobar', [FooBarHandler.KEYWORD, BazHandler.KEYWORD])

    /*
        The static fields below are lists containing one or two scripts.
        During tests, a KeywordVisitor will perform AST transformations on the first script.
        If a second script is available, its AST will be compared with the transformed AST of the first script.
        If the second script is missing, the tests expect that the KeywordVisitor does not change the AST of the first script.
    */

    // No AST transformation expected for this script
    def static script1 = ['''
            bazbaz1 myFirstBaz(type: baz) {
              println 'My baz'
            }
        '''
    ]

    def static script2 = [
            "bazbaz myFirstBaz(type: baz)",
            "bazbaz('myFirstBaz', [type: BigInteger])"
    ]

    def static script3 = ['''
            bazbaz myFirstBaz(foobarType: football, type: baz, bazType: bazooka) {
              println 'My baz'
            }
        ''',
        '''
            bazbaz('myFirstBaz', [foobarType: football, type: BigInteger, bazType: bazooka], {
              println 'My baz'
            })
        '''
    ]

    def static script4 = ['''
            bazbaz myFirstBaz(type: baz) {
              bazaar (bazType: bazzz, foobarType: bar, type: xxxType)
              bazooka (foobarType: noFooBar, bazType: baz)
              barbecue (bazType: bazooka, foobarType: pub)
              football (bazType: bazzz, foobarType: pub)
            }
        ''',
        '''
            bazbaz('myFirstBaz', [type: BigInteger], {
              bazaar (bazType: BigInteger, foobarType: bar, type: xxxType)
              bazooka (foobarType: noFooBar, bazType: BigInteger)
              barbecue (bazType: bazooka, foobarType: BigDecimal)
              football (bazType: bazzz, foobarType: pub)
            })
        '''
    ]

    def static script5 = [
            "foobar myFooBar(type: foo)",
            "foobar('myFooBar', [type: Float])"
    ]

    def static script6 = ['''
            foobar myFooBar(type: bar) {
              bazaar (bazType: bazzz, foobarType: bar, type: xxxType)
              football (bazType: bazzz, foobarType: bar, type: xxxType)
              barbecue (foobarType: foo)
            }
        ''',
        '''
            foobar('myFooBar', [type: BigDecimal], {
              bazaar (bazType: bazzz, foobarType: bar, type: xxxType)
              football (bazType: bazzz, foobarType: BigDecimal, type: xxxType)
              barbecue (foobarType: Float)
            })
        '''
    ]

    def "should handle keywords and substitute aliases" () {
        given:

        def traceTransformed = parser.parse(script[0], 'transformed', true)
        def traceUnchanged = parser.parse(script[-1], 'unchanged', false)

        expect:
        traceTransformed == traceUnchanged

        where:
        script << [script1, script2, script3, script4, script5, script6]
    }




    /*
        The static fields below represent scripts that are expected to fail, because they refer to unknown aliases.
    */

    def static badScript1 = 'bazbaz myBadBaz(type: bad)'

    def static badScript2 = '''
        foobar myBadFooBar(type: bar) {
          football (bazType: bazzz, foobarType: baz, type: xxxType)
        }
    '''

    def "should detect unknown aliases" () {
        when:
        parser.parse(script, 'transformed', true)

        then:
        MultipleCompilationErrorsException mcee = thrown()
        Throwable firstCause = mcee.errorCollector.errors[0].cause
        firstCause instanceof ViewrekaException
        firstCause.message == KeywordVisitor.getUnknownAliasErrorMessage(alias, handlerClass)

        where:
        script     | alias | handlerClass
        badScript1 | 'bad' | BazHandler
        badScript2 | 'baz' | FooBarHandler

    }
}
