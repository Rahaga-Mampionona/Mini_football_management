ALTER TABLE dish
ADD COLUMN IF NOT EXISTS selling_price numeric(10,2);

update dish
set selling_price = 2000.00
where name = 'Salade fraîche';

update dish
set selling_price = 6000.00
where name = 'Poulet grillé';

update dish
set selling_price = null
where name = 'Riz au légumes';

update dish
set selling_price = null
where name = 'Gâteau au chocolat';

update dish
set selling_price = null
where name = 'Salade de fruit'