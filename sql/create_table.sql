-- auto-generated definition
create table user
(
    id          bigint auto_increment comment 'id'
        primary key,
    username    varchar(256)                           null comment '用户名',
    account     varchar(256)                           null comment '账号',
    avatarUrl   varchar(1024)                          null comment '用户头像',
    gender      tinyint                                null comment '性别',
    password    varchar(512)                           not null comment '密码',
    userRole    varchar(256) default 'user'            not null comment '角色 user/admin',
    createTime  datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    editTime    datetime     default CURRENT_TIMESTAMP null comment '编辑时间',
    updateTime  datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除',
    userProfile varchar(512)                           null comment '用户简介'
)
    comment '用户表' collate = utf8mb4_general_ci;

create index idx_account
    on user (account);

create table chart
(
    id          bigint auto_increment comment 'id'
        primary key,
    goal        text                                   null comment '分析目标',
    chartName   varchar(128)                           null comment '图表名称',
    chartData   text                                   null comment '图表数据',
    chartType   varchar(128)                           null comment '图表类型',
    AiGenChart  text                                   null comment 'AI生成图表数据',
    AiGenResult text                                   null comment '生成的分析结论',
    userId      bigint                                 null comment '(创建图表的)用户ID',
    createTime  datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    status      varchar(128) default 'wait'            not null comment 'wait,running,succeed,failed',
    execMessage text                                   null comment '执行信息',
    isDelete    tinyint      default 0                 not null comment '是否删除'
)
    comment '图表表';

-- 图片表
create table picture
(
    id            bigint auto_increment comment 'id'
        primary key,
    url           varchar(512)                       not null comment '图片 url',
    name          varchar(128)                       not null comment '图片名称',
    introduction  varchar(512)                       null comment '简介',
    category      varchar(64)                        null comment '分类',
    tags          varchar(512)                       null comment '标签（JSON 数组）',
    picSize       bigint                             null comment '图片体积',
    picWidth      int                                null comment '图片宽度',
    picHeight     int                                null comment '图片高度',
    picScale      double                             null comment '图片宽高比例',
    picFormat     varchar(32)                        null comment '图片格式',
    userId        bigint                             not null comment '创建用户 id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime      datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除',
    reviewStatus  int      default 0                 not null comment '审核状态：0-待审核; 1-通过; 2-拒绝',
    reviewMessage varchar(512)                       null comment '审核信息',
    reviewerId    bigint                             null comment '审核人 ID',
    reviewTime    datetime                           null comment '审核时间'
)
    comment '图片' collate = utf8mb4_unicode_ci;

create index idx_category
    on picture (category);

create index idx_introduction
    on picture (introduction);

create index idx_name
    on picture (name);

create index idx_reviewStatus
    on picture (reviewStatus);

create index idx_tags
    on picture (tags);

create index idx_userId
    on picture (userId);


