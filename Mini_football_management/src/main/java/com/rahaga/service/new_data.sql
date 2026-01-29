insert into dishingredient (id_dish, id_ingredient, quantity_required, unit) VALUES (1,1, 0.20, 'KG'),
                                                                                    (1,2,0.15,'KG'),
                                                                                    (2,3,1,'KG'),
                                                                                    (4,4,0.30,'KG'),
                                                                                    (4,5,0.20,'KG');

update dish
set selling_price = 3500.00 where name='Salade fraîche';
update dish set selling_price=12000.00 where name='Poulet grillé';
update dish set selling_price=8000.00 where name= 'Gâteau au chocolat ';