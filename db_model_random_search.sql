-- database for storing analysis results
use stefan_bocko;

-- create tables for storing data

-- delete tables
drop table if exists `knn_rand`;
drop table if exists `decision_tree_rand`;
drop table if exists `logistic_regression_rand`;
drop table if exists `svm_rand`;
drop table if exists `dataset_rand`;

-- table DATASET contains info about analyzed dataset
create table dataset_rand(id int not null auto_increment,
					 `name` varchar(50),
					 primary key (id));

-- table KNN contains search results for knn algorithm
create table knn_rand(id int not null auto_increment,
				 dataset_id int not null,
				 rmse double,
				 k int,
				 primary key (id),
				 foreign key (dataset_id) references dataset_rand(id));

-- table DECISION_TREE contains search results for decision tree algorithm
create table decision_tree_rand(id int not null auto_increment,
				 dataset_id int not null,
				 rmse double,
				 confidence_factor double,
				 min_number_of_instances_per_leaf int,
				 unpruned boolean,
				 primary key (id),
				 foreign key (dataset_id) references dataset_rand(id));

-- table LOGISTIC_REGRESSION contains search results for logistic regression algorithm
create table logistic_regression_rand(id int not null auto_increment,
				 dataset_id int not null,
				 rmse double,
				 ridge double,
				 max_number_of_iterations int,
				 primary key (id),
				 foreign key (dataset_id) references dataset_rand(id));

-- table SVM contains search results for SMO (SVM) algorithm
create table svm_rand(id int not null auto_increment,
				 dataset_id int not null,
				 rmse double,
				 kernel varchar(30),
				 complexity_constant double,
				 epsilon_err double,
				 primary key (id),
				 foreign key (dataset_id) references dataset_rand(id));

select * from dataset_rand;
select count(*) from decision_tree_rand;
select count(*) from logistic_regression_rand;
select count(*) from knn_rand;
select count(*) from svm_rand;
select * from (select count(*) pocet from svm_rand group by dataset_id) p where p.pocet != 11880;
select count(*) from svm_rand where rmse != -1;
-- select distinct(name) from dataset;

select count(*) from decision_tree_rand where rmse = 0;

select * from dataset;
select count(*) from knn where dataset_id=56 and rmse < 0.34952013135066345;