select exp_type
  , sum(reap) eap
  , sum(reca) eca
  , sum(rlac) lac
  , sum(rmena) mena
  , sum(rna) na
  , sum(rsa) sa
  , sum(rssa) ssa
from (
    select calendar_year, exp_type
      , (case when region = 'East Asia & Pacific' then le else 0 end) reap
      , (case when region = 'Europe & Central Asia' then le else 0 end) reca
      , (case when region = 'Latin America & Caribbean' then le else 0 end) rlac
      , (case when region = 'Middle East & North Africa' then le else 0 end) rmena
      , (case when region = 'North America' then le else 0 end) rna
      , (case when region = 'South Asia' then le else 0 end) rsa
      , (case when region = 'Sub-Saharan Africa' then le else 0 end) rssa
    from (
        select 'total' exp_type, calendar_year, region, avg(life_exp) le
        from v_indicator where calendar_year=:prmYear group by calendar_year, region
        union
        select 'male' exp_type, calendar_year, region, avg(life_exp_male) le
        from v_indicator where calendar_year=:prmYear group by calendar_year, region
        union
        select 'female' exp_type, calendar_year, region, avg(life_exp_female) le
        from v_indicator where calendar_year=:prmYear group by calendar_year, region
    ) as t
) as aggr
group by exp_type
