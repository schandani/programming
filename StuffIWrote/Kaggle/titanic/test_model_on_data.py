'''
8/10/14, 8:24 AM
Ethan Petuchowski
test_model_on_data.py

Full framework for testing different models for the Titanic Kaggle competition
'''
from scipy.spatial import distance
from sklearn.cross_validation import StratifiedKFold, KFold
from sklearn.ensemble import GradientBoostingClassifier, RandomForestClassifier, ExtraTreesClassifier
from sklearn.linear_model import LogisticRegression
import pandas as pd, numpy as np, csv, os, copy

# go to this file's location
# stackoverflow.com/questions/5137497
os.chdir(os.path.dirname(os.path.realpath(__file__)))

class Titanic(object):

    @staticmethod
    def preprocess(df):
        """ convert the categorical variables to dummy variables """
        assert isinstance(df, pd.DataFrame), 'can only preprocess a dataframe'
        # First of all turn Sex from a string-factor variable into a simple Indicator
        df.Sex = (df.Sex == 'female').astype(int)
        # Label missing Embarked as having embarked from most common place
        # if there are null values
        if len(df.Embarked[ df.Embarked.isnull() ]) > 0:
            # all rows with null values, should instead receive the mode value
            df.Embarked[ df.Embarked.isnull() ] = df.Embarked.dropna().mode().values
        '''
        * Instead of translating categories to numbers, make dummy-variable columns
                and then remove the redundant one
        * Specifically, we will go
                from Categorical(['C', 'Q', 'S']), =-> to Indicator('C'), Indicator('Q')
        '''
        df = pd.concat([df,pd.get_dummies(df.Embarked)], axis=1).drop(['S'], axis=1)
        # All the ages with no data -> make the median of all Ages
        median_age = df.Age.dropna().median()
        if len(df.Age[ df.Age.isnull() ]) > 0:
            df.loc[ df.Age.isnull(), 'Age' ] = median_age

        df.Cabin = df.Cabin.isnull()
        # this used to have 'Cabin', but I changed it just above
        df = df.drop(['Name', 'Ticket', 'Embarked', 'PassengerId'], axis=1)
        return df

    @staticmethod
    def fill_fares(df):
        """
        Fill missing Fares with median of their respective Pclass

        Note: This doesn't need to be done for the Training Set because
              none of the fares are missing in that set
        """
        assert isinstance(df, pd.DataFrame), 'df must be a DataFrame, was a %s' % type(df)
        if len(df.Fare[ df.Fare.isnull() ]) > 0:
            median_fare = np.zeros(3) # => array([ 0.,  0.,  0.])
            for f in range(0,3):
                median_fare[f] = df[ df.Pclass == f+1 ]['Fare'].dropna().median()
                df.loc[ (df.Fare.isnull()) & (df.Pclass == f+1 ), 'Fare'] = median_fare[f]
        return df

    @staticmethod
    def fraction_incorrect(a, b):
        return 1 - distance.cityblock(a, b) / len(b)

    def mean_k_fold_cross_validate(self, attr=None, target=None, model=None, k=8):
        ''' get results of using the provided model on each cv-segment '''
        if attr is None:   attr   = self.train_attr
        if target is None: target = self.train_target
        if model is None:  model  = self.model
        results = np.zeros(k)
        cv = KFold(len(attr), n_folds=k, indices=False)
        for i, (train_idx, validation_idx) in enumerate(cv):
            model.fit(attr[train_idx], target[train_idx])
            predicted_validation = model.predict(attr[validation_idx])
            truth_validation = self.train_target[validation_idx]
            results[i] = self.fraction_incorrect(truth_validation, predicted_validation)
        return results.mean()

    def model_name(self):
        full_str = str(type(self.model))
        left = full_str.rfind('.')+1
        right = full_str.rfind("'")
        return full_str[left:right]

    def csv_named(self, name):
        return self.out_loc+'/'+name+'.csv'

    def __init__(self,
                 train_loc='data/train.csv',
                 test_loc='data/test.csv',
                 out_loc='data/output',
                 out_name='logReg',
                 model=None,
                 pipeline=False):
        '''
        1. read data
        2. preprocess data
        3. print cross-validation scores for model on data
        4. save csv of predictions for test-set
        '''
        self.out_loc = out_loc
        self.out_file = self.csv_named(out_name)
        self.model = model if model is not None else LogisticRegression()
        print 'Model: %s' % self.model_name()
        self.raw_train_df = pd.read_csv(train_loc, header=0)
        self.raw_test_df = pd.read_csv(test_loc, header=0)

        # collect the test data's PassengerIds before dropping it
        self.ids = self.raw_test_df.PassengerId.values

        # preprocess the data
        self.prep_train_df = self.preprocess(self.raw_train_df)
        self.prep_test_df = self.preprocess(self.raw_test_df)
        self.prep_test_df = self.fill_fares(self.prep_test_df)

        # convert dataframes back to numpy arrays
        self.train_arr = self.prep_train_df.values
        self.test_attr = self.prep_test_df.values

        # separate record-attributes from record-targets
        self.train_attr = self.train_arr[:,1:]
        self.train_target = self.train_arr[:,0]

        self.pipeline_ensemble() if pipeline else self.use_given_model()

    def use_given_model(self):
        # cross-validate
        print 'Mean CV result: %.3f\n' % self.mean_k_fold_cross_validate()
        self.model.fit(self.train_attr, self.train_target)
        survival_predictions = self.model.predict(self.test_attr).astype(int)
        self.write_submission(survival_predictions, self.out_file)

    def write_submission(self, predictions, name):
        with open(name, 'wb') as predictions_file:
            csv_writer = csv.writer(predictions_file)
            csv_writer.writerow(['PassengerId', 'Survived'])
            csv_writer.writerows(zip(self.ids, map(int, predictions)))

    def pipeline_ensemble(self):
        '''
        blended trainer
        based on https://github.com/emanuele/kaggle_pbr/blob/master/blend.py
        '''
        skf = list(StratifiedKFold(self.train_target, n_folds = 10))

        n_est = 100

        gini_opts = {
            'n_estimators'      : n_est,
            'n_jobs'            : -1,
            'criterion'         : 'gini',
            'max_depth'         : 4,
            'min_samples_leaf'  : 4,
            'min_samples_split' : 4
        }

        entropy_opts = copy.deepcopy(gini_opts)
        entropy_opts['criterion'] = 'entropy'

        boost_opts = copy.deepcopy(gini_opts)
        del boost_opts['criterion']
        del boost_opts['n_jobs']
        boost_opts.update({
            'learning_rate' : 0.02,
            'subsample'     : 0.8
        })

        classifiers = [RandomForestClassifier(**gini_opts),
                       RandomForestClassifier(**entropy_opts),
                       ExtraTreesClassifier(**gini_opts),
                       ExtraTreesClassifier(**entropy_opts),
                       GradientBoostingClassifier(**boost_opts)]

        # This is a new feature-set we are creating as training data by running
        # the training data through classifiers. Each column is the output of a
        # single classifier trained on /other/ data (i.e. the value of each row
        # is created when this row a hold-out element).
        dataset_blend_train = np.zeros((self.train_attr.shape[0], len(classifiers)))

        # This is the matrix we will map the test-dataset to using the 5
        # classifiers chosen above. Then we will train on the above matrix, and
        # using the function created by that, map this matrix to our submission
        # vector.
        dataset_blend_test = np.zeros((self.test_attr.shape[0], len(classifiers)))

        for j, clf in enumerate(classifiers):
            print j, clf
            dataset_blend_test_j = np.zeros((self.test_attr.shape[0], len(skf)))
            for i, (train_idx, validation_idx) in enumerate(skf):
                print "Fold", i

                # split the given training set into
                # sub-training and validation sets
                X_train = self.train_attr[train_idx]
                y_train = self.train_target[train_idx]
                X_validation = self.train_attr[validation_idx]
                y_validation = self.train_target[validation_idx]  # not used

                # train current classifier on current train/validate split
                clf.fit(X_train, y_train)

                # run trained classifier on the validation set
                y_validation_predicted = clf.predict_proba(X_validation)[:,1] # col 0 is P(0), 1 is P(1)

                # In the column associated with this classifier in the pipeline,
                # store the predicted probabilities for the rows holding this
                # validation set.  All rows will at some point be in the
                # validation set so all rows will be filled in in this manner.
                dataset_blend_train[validation_idx, j] = y_validation_predicted

                # Run the trained classifier on the true test set on every
                # round of validation, and store all the results.
                dataset_blend_test_j[:, i] = clf.predict_proba(self.test_attr)[:,1]

            # (Just) above, we ran all of the sub-training set classifiers on
            # the test set. Now we will average the result on each row over all
            # of those sub-set runs. This will give us one value per row per
            # trained classifier.
            dataset_blend_test[:, j] = dataset_blend_test_j.mean(1)

        # Now we train a logit model on the /outputs/ of the "pipeline"
        # classifiers.  This model will ideally "learn" how weightedly combine
        # the classifiers' opinions into one final opinion.  Then we write the
        # output of this logit model to a CSV.
        clf = LogisticRegression()
        print '\nMean Blent CV result: %.3f' % self.mean_k_fold_cross_validate(attr=dataset_blend_train, model=clf)
        clf.fit(dataset_blend_train, self.train_target)
        y_submission = clf.predict(dataset_blend_test)
        self.write_submission(y_submission, self.csv_named('blent'))


if __name__ == '__main__':
    Titanic(model=LogisticRegression(), out_name='log_reg', pipeline=False)

    # TODO why are my CV results always 1.000 now??

    # Titanic(model=GradientBoostingClassifier(max_features=3, min_samples_leaf=5, min_samples_split=5), out_name='grad_boost')
    # Titanic(model=RandomForestClassifier(n_estimators=120, n_jobs=-1), out_name='rand_forest')
