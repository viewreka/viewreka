select '%' name, 'All regions' description from SYSIBM.SYSDUMMY1
union
select name, name description from ta_region
