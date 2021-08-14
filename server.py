from flask import Flask
import pandas as pd

app = Flask('test')

@app.route('/get_result/<site>')
def get_result(site, methods=['GET']):
    return site.upper()

@app.route('/get_result_csv/<file>')
def get_result_csv(file, methods=['GET']):
    df = pd.read_csv("data/" + file, header=None)
    new_path = file[:-4] + '_res.csv'
    df[0][0] = 'test'
    df.to_csv("data/" + new_path, header=None, index=False)
    return new_path
    
app.run()
