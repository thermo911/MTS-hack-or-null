from catboost import CatBoostClassifier
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer
from sklearn.model_selection import cross_val_score, KFold
from sklearn import metrics
from sklearn.metrics import accuracy_score, roc_auc_score, precision_score, recall_score

class Model():
    def __int__(self, depth=10):
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

        self.model = CatBoostClassifier(depth=depth)
    def fit(self, X, y, fname=None):
        X = self.process(X)
        self.model.fit(X, y)
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
