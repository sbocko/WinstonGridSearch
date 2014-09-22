-- database for storing analysis results
use stefan_bocko;

-- create tables for storing data

-- delete tables
drop table if exists `knn_rand`;
drop table if exists `decision_tree_rand`;
drop table if exists `logistic_regression_rand`;
drop table if exists `svm_rand`;
drop table if exists `similarity_best_rand`;
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

-- table similarity_best_rand contains search results for SMO (SVM) algorithm
create table similarity_best_rand(id int not null auto_increment,
				 dataset_id int not null,
				 dataset_similar_id int not null,
				 rmse double,
				 similarity_err double,
				 primary key (id),
				 foreign key (dataset_id) references dataset_rand(id));

select name from dataset_rand;
select count(*) from decision_tree_rand;
select count(*) from logistic_regression_rand;
select count(*) from knn_rand;
select count(*) from svm_rand;
select * from similarity_best_rand;
select * from (select count(*) pocet from svm_rand group by dataset_id) p where p.pocet != 11880;
select count(*) from svm_rand where rmse != -1;
-- select distinct(name) from dataset;

select count(*) from decision_tree_rand where rmse = 0;

select * from dataset_rand where name='original.data';
select * from logistic_regression_rand where dataset_id=42;
select count(*) from knn where dataset_id=56 and rmse < 0.34952013135066345;

select * from decision_tree_rand where dataset_id=59 order by rmse;

select count(*) from similarity_best_rand;
select * from similarity_best_rand;

-- report for best search 
select t1.dataset_id, t1.rmse 'similarity search rmse', sum(t1.better_results)+sum(t2.better_results)+sum(t3.better_results)+sum(t4.better_results) better, 
		sum(t1.all_results)+sum(t2.all_results)+sum(t3.all_results)+sum(t4.all_results) 'all'
	from (select t.dataset_id, best.rmse, count(t.dataset_id) better_results, ta.all_results from knn_rand t
		join (select dataset_id, dataset_similar_id, rmse, min(similarity_err) from similarity_best_rand group by dataset_id) best 
		on best.dataset_id = t.dataset_id
		join (select dataset_id, count(*) all_results from knn_rand group by dataset_id) ta
		on t.dataset_id = ta.dataset_id
		where t.rmse < best.rmse group by t.dataset_id) t1
	join (select d.dataset_id, best.rmse, count(d.dataset_id) better_results, da.all_results from decision_tree_rand d
		join (select dataset_id, dataset_similar_id, rmse, min(similarity_err) from similarity_best_rand group by dataset_id) best 
		on best.dataset_id = d.dataset_id
		join (select dataset_id, count(*) all_results from decision_tree_rand group by dataset_id) da
		on d.dataset_id = da.dataset_id
		where d.rmse < best.rmse group by d.dataset_id) t2
	on t1.dataset_id = t2.dataset_id
	join (select l.dataset_id, best.rmse, count(l.dataset_id) better_results, la.all_results from logistic_regression_rand l
		join (select dataset_id, dataset_similar_id, rmse, min(similarity_err) from similarity_best_rand group by dataset_id) best 
		on best.dataset_id = l.dataset_id
		join (select dataset_id, count(*) all_results from logistic_regression_rand group by dataset_id) la
		on l.dataset_id = la.dataset_id
		where l.rmse < best.rmse group by l.dataset_id) t3
	on t1.dataset_id = t3.dataset_id
	join (select s.dataset_id, best.rmse, count(s.dataset_id) better_results, sa.all_results from svm_rand s
		join (select dataset_id, dataset_similar_id, rmse, min(similarity_err) from similarity_best_rand group by dataset_id) best 
		on best.dataset_id = s.dataset_id
		join (select dataset_id, count(*) all_results from svm_rand group by dataset_id) sa
		on s.dataset_id = sa.dataset_id
		where s.rmse < best.rmse group by s.dataset_id) t4
	on t1.dataset_id = t4.dataset_id group by t1.dataset_id;


-- knn_rand stats
select t.dataset_id, best.rmse, count(t.dataset_id) better_results, ta.all_results from knn_rand t
	join (select dataset_id, dataset_similar_id, rmse, min(similarity_err) from similarity_best_rand group by dataset_id) best 
		on best.dataset_id = t.dataset_id
	join (select dataset_id, count(*) all_results from knn_rand group by dataset_id) ta
		on t.dataset_id = ta.dataset_id
		where t.rmse < best.rmse group by t.dataset_id;

-- dec_tree stats
select d.dataset_id, best.rmse, count(d.dataset_id) better_results, da.all_results from decision_tree_rand d
	join (select dataset_id, dataset_similar_id, rmse, min(similarity_err) from similarity_best_rand group by dataset_id) best 
		on best.dataset_id = d.dataset_id
	join (select dataset_id, count(*) all_results from decision_tree_rand group by dataset_id) da
		on d.dataset_id = da.dataset_id
		where d.rmse < best.rmse group by d.dataset_id;

-- log_regression stats
select l.dataset_id, best.rmse, count(l.dataset_id) better_results, la.all_results from logistic_regression_rand l
	join (select dataset_id, dataset_similar_id, rmse, min(similarity_err) from similarity_best_rand group by dataset_id) best 
		on best.dataset_id = l.dataset_id
	join (select dataset_id, count(*) all_results from logistic_regression_rand group by dataset_id) la
		on l.dataset_id = la.dataset_id
		where l.rmse < best.rmse group by l.dataset_id;

-- svm stats
select s.dataset_id, best.rmse, count(s.dataset_id) better_results, sa.all_results from svm_rand s
	join (select dataset_id, dataset_similar_id, rmse, min(similarity_err) from similarity_best_rand group by dataset_id) best 
		on best.dataset_id = s.dataset_id
	join (select dataset_id, count(*) all_results from svm_rand group by dataset_id) sa
		on s.dataset_id = sa.dataset_id
		where s.rmse < best.rmse group by s.dataset_id;


select dataset_id, count(*) all_results from knn_rand group by dataset_id








