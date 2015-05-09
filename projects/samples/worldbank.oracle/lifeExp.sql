select calendar_year, life_exp, life_exp_male, life_exp_female from v_indicator
where country_name=:prmCountry
