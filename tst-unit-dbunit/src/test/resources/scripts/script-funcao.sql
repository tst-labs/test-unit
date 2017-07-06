DROP ALIAS IF EXISTS SOMAR;
CREATE ALIAS SOMAR AS 
$$
	int somar(int primeiro, int segundo) { 
		return primeiro + segundo;
	}
$$;
