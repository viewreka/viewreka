select c.name as country
     , sum(cl.percentage * (cl.isOfficial = TRUE)) as official
     , sum(cl.percentage * (cl.isOfficial != TRUE)) as unofficial
from countryLanguage cl
join country c on (cl.countryCode = c.code)
where c.name = :prmCountry
group by c.name
order by c.name
