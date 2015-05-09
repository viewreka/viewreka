select * from
  (select * from
    (select calendar_year, income
     , avg(life_exp) le
     , avg(life_exp_male) lem
     , avg(life_exp_female) lef
    from v_indicator
    where calendar_year = :prmYear
    group by calendar_year, income)
  unpivot (exp for exp_type in (le as 'total', lem as 'male', lef as 'female')))
pivot (
  sum(exp)
  for income in (
    'Low income' as l, 
    'Lower middle income' as lm, 
    'Upper middle income' as um, 
    'High income: nonOECD' as hno,
    'High income: OECD' as ho
  )
)
