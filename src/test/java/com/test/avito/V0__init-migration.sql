create table if not exists user_data
(
    id         bigint generated always as identity primary key,
    username   varchar(255) unique,
    password   varchar(255) not null,
    role       varchar(16) check ( role in ('USER', 'ADMIN') ),
    coins      bigint default 1000
);

create table if not exists merch
(
    id    bigint generated always as identity primary key,
    name  varchar(64) unique not null,
    price bigint             not null
);

create table if not exists inventory
(
    id       bigint generated always as identity primary key,
    item_id  bigint not null references merch (id) on delete no action,
    user_id  bigint not null references user_data (id) on delete no action,
    quantity bigint not null default 1
);

insert into merch (name, price)
values ('t-shirt', 80),
       ('cup', 20),
       ('book', 50),
       ('pen', 10),
       ('powerbank', 200),
       ('hoody', 300),
       ('umbrella', 200),
       ('socks', 10),
       ('wallet', 50),
       ('pink-hoody', 500);

create table if not exists transaction_logs
(
    id             bigint generated always as identity primary key,
    user_id        bigint       not null references user_data (id) on delete no action,
    second_user_id bigint references user_data (id) on delete no action,
    details        text         not null,
    coins          bigint       not null,
    description    varchar(255) not null,
    date           timestamp default now()
);

CREATE OR REPLACE FUNCTION transfer_money(
    sender_id BIGINT,
    receiver_id BIGINT,
    amount BIGINT,
    description VARCHAR(255))
    RETURNS text AS
$$
DECLARE
    sender_money   BIGINT;
    receiver_money BIGINT;
BEGIN
    IF amount <= 0 THEN
        RAISE EXCEPTION 'Amount must be greater than 0';
    END IF;

    SELECT coins
    INTO sender_money
    FROM user_data
    WHERE id = sender_id
        for update;

    IF sender_money < amount THEN
        RAISE EXCEPTION 'Insufficient funds';
    END IF;

    SELECT coins
    INTO receiver_money
    FROM user_data
    WHERE id = receiver_id;

    UPDATE user_data
    SET coins = coins - amount
    WHERE id = sender_id;

    UPDATE user_data
    SET coins = coins + amount
    WHERE id = receiver_id;

    INSERT INTO transaction_logs (user_id, details, coins, description, second_user_id)
    VALUES (sender_id, 'transfer_to', amount, description, receiver_id);

    INSERT INTO transaction_logs (user_id, details, coins, description, second_user_id)
    VALUES (receiver_id, 'transfer_from', amount, description, sender_id);
    return 'Успешно!';

EXCEPTION
    when OTHERS then
        rollback;
        raise;
END ;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION buy_merch(
    user_id_input BIGINT,
    merch_id_input BIGINT)
    RETURNS text AS
$$
DECLARE
    price_merch BIGINT;
    user_money  BIGINT;
BEGIN
    select m.price into price_merch from merch m where m.id = merch_id_input;
    select u.coins into user_money from user_data u where u.id = user_id_input for update;
    if user_money < price_merch then
        raise exception 'Not enough coins';
    end if;

    if (select count(*) from inventory i where i.user_id = user_id_input and i.item_id = merch_id_input) > 0 then
        update inventory i set quantity = quantity + 1 where i.user_id = user_id_input and i.item_id = merch_id_input;
    else
        insert into inventory (user_id, item_id, quantity) values (user_id_input, merch_id_input, 1);
    end if;

    update user_data u set coins = coins - price_merch where u.id = user_id_input;

    INSERT INTO transaction_logs (user_id, details, coins, description)
    VALUES (user_id_input, 'buy', price_merch, 'покупочка');

    return 'Успешно!';

EXCEPTION
    when OTHERS then
        raise;

END;
$$ LANGUAGE plpgsql;
