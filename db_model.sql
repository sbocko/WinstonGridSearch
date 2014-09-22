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
select * from svm;
select * from knn;

select count(*) from dataset;
select count(*) from svm;
select count(*) from knn;
select count(*) from decision_tree;
select count(*) from logistic_regression;

-- last inserted object 
select table_schema,table_name,max_time from information_schema.tables t1 JOIN 
 (select MAX(t2.create_time) AS max_time FROM 
  information_schema.tables t2 where  
  table_schema ='stefan_bocko') as t3  
on t1.create_time = t3.max_time;

-- datasety so vsetkymi vypoctami
select distinct(name) from dataset d join
	knn k on d.id = k.dataset_id join
	decision_tree t on d.id = t.dataset_id join
	logistic_regression l on d.id = l.dataset_id join
	svm s on d.id = s.dataset_id;

-- datasety s troma vypoctami  
select distinct(d.id), d.name from dataset d join
	knn k on d.id = k.dataset_id join
	decision_tree t on d.id = t.dataset_id join
	logistic_regression l on d.id = l.dataset_id;

-- pocty vysledkov pre jednotlive datasety
select dataset_id, count(*) from knn kn where kn.dataset_id in (select distinct(d.id) from dataset d join
	knn k on d.id = k.dataset_id join
	decision_tree t on d.id = t.dataset_id join
	logistic_regression l on d.id = l.dataset_id) group by kn.dataset_id;

-- pocty vysledkov pre jednotlive datasety s default hodnotami
select dataset_id, count(*) from decision_tree kn where kn.dataset_id in (select distinct(d.id) from dataset d join
	knn k on d.id = k.dataset_id join
	decision_tree t on d.id = t.dataset_id join
	logistic_regression l on d.id = l.dataset_id) and kn.min_number_of_instances_per_leaf = 2 and kn.confidence_factor = 0.25;

select min_number_of_instances_per_leaf parameter, sum(rmse) suma from decision_tree group by parameter order by suma;

select count(*) from decision_tree;

select * from decision_tree where rmse=0;