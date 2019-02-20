# easy-dao
基于spring 简单封装 数据操作层

#### 项目说明

目前实现了如下功能：

1. 数据库的基本操作（增删查改）
  
2. 多数据源支持，并实现在项目中切换数据库
  
      通过注解的方式实现，实现在类上定义数据源，方法中选择数据源 
      
      类上注解 @DataSourceConfig(name = DataSourceConstant.DATA_SOURCE_A)
      方法上注解 @DataSource
      
      通过使用 BaseDao.use（数据源的名称）切换

#### 项目使用
 在springBoot环境下使用easy-dao项目
 
 首先需要创建数据源，例子中我们创建了beanName为dataSourceOne 数据源     
    /**
     * 使用 easy-dao
     * @return
     */
    @Bean
    public DataSourceHolder dataSourceHolder(@Qualifier("dataSourceOne") DataSource dataSource) {
        DataSourceHolder dataSourceHolder = new DataSourceHolder();
        // 传入默认的数据源
        dataSourceHolder.setDefaultTargetDataSource(dataSource);
        // 创建map存数据源信息
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceConstant.DATA_SOURCE_A,dataSource);
        dataSourceHolder.setTargetDataSources(targetDataSources);
        return dataSourceHolder;
    }
### 留言

由于在平时业余时间开发，没有经过洗礼难免会出现各种问题，如果有问题可以提issue
