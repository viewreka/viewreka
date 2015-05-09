select * from
  (select * from
    (select calendar_year, region
     , avg(life_exp) le
     , avg(life_exp_male) lem
     , avg(life_exp_female) lef
    from v_indicator
    where calendar_year = :prmYear
    group by calendar_year, region)
  unpivot (exp for exp_type in (le as 'total', lem as 'male', lef as 'female')))
pivot (
  sum(exp)
  for region in (
    'East Asia & Pacific' as eap, 
    'Europe & Central Asia' as eca, 
    'Latin America & Caribbean' as lac, 
    'Middle East & North Africa' as mena, 
    'North America' as na, 
    'South Asia' as sa, 
    'Sub-Saharan Africa' as ssa
  )
)
