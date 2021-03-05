import csv
import pandas as pd
import re

with open ("srar.csv", "r") as file:
    df = pd.read_csv(file, delimiter = ",")
    print(df)