-- database for storing analysis results
use stefan_bocko;

-- create tables for storing data

-- delete tables
drop table if exists `knn`;
drop table if exists `decision_tree`;
drop table if exists `logistic_regression`;
drop table if exists `svm`;
drop table if exists `dataset`;

-- table DATASET contains info about analyzed dataset
create table dataset(id int not null auto_increment,
					 `name` varchar(50),
					 primary key (id));

-- table KNN contains search results for knn algorithm
create table knn(id int not null auto_increment,
				 dataset_id int not null,
				 rmse double,
				 k int,
				 primary key (id),
				 foreign key (dataset_id) references dataset(id));

-- table DECISION_TREE contains search results for decision tree algorithm
create table decision_tree(id int not null auto_increment,
				 dataset_id int not null,
				 rmse double,
				 confidence_factor double,
				 min_number_of_instances_per_leaf int,
				 unpruned boolean,
				 primary key (id),
				 foreign key (dataset_id) references dataset(id));

-- table LOGISTIC_REGRESSION contains search results for logistic regression algorithm
create table logistic_regression(id int not null auto_increment,
				 dataset_id int not null,
				 rmse double,
				 ridge double,
				 max_number_of_iterations int,
				 primary key (id),
				 foreign key (dataset_id) references dataset(id));

-- table SVM contains search results for SMO (SVM) algorithm
create table svm(id int not null auto_increment,
				 dataset_id int not null,
				 rmse double,
				 kernel varchar(30),
				 complexity_constant double,
				 epsilon_err double,
				 primary key (id),
				 foreign key (dataset_id) references dataset(id));

show tables;
select * from dataset;
select * from knn;