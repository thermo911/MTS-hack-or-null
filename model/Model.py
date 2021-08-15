from catboost import CatBoostClassifier
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer
from sklearn.model_selection import cross_val_score, KFold
from sklearn.metrics import accuracy_score, roc_auc_score, precision_score, recall_score

class Model():
    def __init__(self, depth=8, metric_period=100):
        self.model = CatBoostClassifier()
        self.fname_default = '../saved/'
        self.vocab = list('-.0123456789_abcdefghijklmnopqrstuvwxyz')

        self.vectorizer = CountVectorizer(
            analyzer="char",
            tokenizer=None,
            preprocessor=None,
            stop_words=None,
            max_features=5000,
            vocabulary=self.vocab
        )

        self.model = CatBoostClassifier(depth=depth,
                                        metric_period=metric_period,
                                        )

    def fit(self, X, y, fname=None, verbose=False, save=False):
        X = self.process(X)
        self.model.fit(X, y, verbose=verbose)
        if save:
            self.save_model(fname)


    def process(self, X):
        return self.vectorizer.fit_transform(X)

    def predict(self, hosts):
        X = self.process(hosts)
        return self.model.predict(X)

    def save_model(self, fname=None):
        if fname is None:
            fname = self.fname_default
        self.model.save_model(fname)

    def load_model(self, fname=None):
        if fname is None:
            fname = self.fname_default
        self.model.load_model(fname)

    def evaluate(self, X, y_true):
        y_pred = self.model.predict(X)

        metrics = {
            'accuracy': accuracy_score(y_pred, y_true),
            'roc_auc': roc_auc_score(y_pred, y_true),
            'recall': recall_score(y_pred, y_true),
            'precision': precision_score(y_pred, y_true),
        }

        return metrics