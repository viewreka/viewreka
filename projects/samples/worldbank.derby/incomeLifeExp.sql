select exp_type, sum(li) l, sum(lmi) lm, sum(umi) um, sum(hino) hno, sum(hio) ho from (
	select exp_type, calendar_year
	, (case when income = 'Low income' then le else 0 end) li
	, (case when income = 'Lower middle income' then le else 0 end) lmi
	, (case when income = 'Upper middle income' then le else 0 end) umi
	, (case when income = 'High income: nonOECD' then le else 0 end) hino 
	, (case when income = 'High income: OECD' then le else 0 end) hio
	from (
		select 'total' exp_type, calendar_year, income, avg(life_exp) le
		from v_indicator where calendar_year=:prmYear group by calendar_year, income
		union
		select 'male' exp_type, calendar_year, income, avg(life_exp_male) le
		from v_indicator where calendar_year=:prmYear group by calendar_year, income
		union
		select 'female' exp_type, calendar_year, income, avg(life_exp_female) le
		from v_indicator where calendar_year=:prmYear group by calendar_year, income
	) as t
) as aggr
group by exp_type
