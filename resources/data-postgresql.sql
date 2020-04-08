DROP TABLE member IFEXIST;

CREATE TABLE public.member  (
    firstname VARCHAR(20),
    lastname VARCHAR(20)
);


INSERT INTO public.member(
	firstname, lastname)
	VALUES ('aa', 'ss');
	
