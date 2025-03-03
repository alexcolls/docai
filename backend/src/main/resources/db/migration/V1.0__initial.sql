create table document (
    id varchar(255) not null,
    created_at timestamp(6) not null,
    name varchar(255),
    updated_at timestamp(6) not null,
    primary key (id)
);

create table document_type (
    id varchar(255) not null,
    created_at timestamp(6) not null,
    name varchar(255) not null,
    prompt TEXT,
    updated_at timestamp(6) not null,
    primary key (id)
);

create table document_type_samples (
    document_type_id varchar(255) not null,
    samples_id varchar(255) not null
);

create table result (
    id varchar(255) not null,
    created_at timestamp(6) not null,
    engine varchar(255),
    json_response TEXT,
    prompt TEXT,
    raw_response TEXT,
    total_tokens integer,
    updated_at timestamp(6) not null,
    document_id varchar(255),
    document_type_id varchar(255),
    primary key (id)
);

create table result_samples (
    result_id varchar(255) not null,
    samples_id varchar(255) not null
);

create table sample (
    id varchar(255) not null,
    created_at timestamp(6) not null,
    response TEXT,
    updated_at timestamp(6) not null,
    document_id varchar(255),
    primary key (id)
);

-- Add constraints
alter table if exists document_type_samples drop constraint if exists UK6cpq0dax88i98r9tp1durql3y;

alter table if exists document_type_samples add constraint UK6cpq0dax88i98r9tp1durql3y unique (samples_id);

alter table if exists document_type_samples add constraint FKeru3y8uj5mvd6qtludmhqlu6m 
    foreign key (samples_id) references sample (id) on delete cascade;

alter table if exists document_type_samples add constraint FKbahuek910vxw4sv8u81ygxc9w 
    foreign key (document_type_id) references document_type (id) on delete cascade;

alter table if exists result add constraint FKfqnec0ti0fvnceparngqhbahs 
    foreign key (document_id) references document (id) on delete cascade;

alter table if exists result add constraint FK2jv0r56s9hy7x09fm7jgyirgp 
    foreign key (document_type_id) references document_type (id) on delete cascade;

alter table if exists result_samples add constraint FKfyyff7w5r682m8n03ttm0w9wl 
    foreign key (samples_id) references sample (id) on delete cascade;

alter table if exists result_samples add constraint FK944i1yhcvs6wyp6fteowo2l0l 
    foreign key (result_id) references result (id) on delete cascade;

alter table if exists sample add constraint FKhihrcav4dck6ab8s2xsjip2k7 
    foreign key (document_id) references document (id) on delete cascade;
