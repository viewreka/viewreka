select name from v_country
where region like :prmRegion
and income like :prmIncome
order by name