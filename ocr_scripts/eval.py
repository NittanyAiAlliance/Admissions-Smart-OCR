import numpy as np
from ocr_model import process_ocr
from tqdm.auto import tqdm
import matplotlib
import matplotlib.pyplot as plt

# Load data
fp = "./eval_data.npy"
EVAL_DATA = np.load(fp, allow_pickle=True).tolist()

# Setup metrics
totals = {}
hits = {}
accuracy = {}

# Function to evaluate model accuracy against evaluation set
def eval():

    # Iterate and process
    for data in tqdm(EVAL_DATA):
        line = process_ocr(data[0])["Line 0"] # Run OCR on text
        exp = data[1] # Save expected array of label pairs
        for label in exp:
            this_label = label[0]
            this_text = label[1]
            found = False
            for entry in range(1, len(line)): # Iterate returned labels
                if(line[entry][0] == this_label and line[entry][1] == this_text):
                    inc_hits(this_label)
                    found = True
            if(not found):
                inc_total(this_label)

    calc_accuracy()
    print_dicts()
    plot_eval()

# Function to increment hits
def inc_hits(lbl):
    if lbl in hits.keys():
        hits[lbl] = hits[lbl] + 1
    else:
        hits[lbl] = 1
    inc_total(lbl)

# Function to increment total
def inc_total(lbl):
    if lbl in totals.keys():
        totals[lbl] = totals[lbl] + 1
    else:
        totals[lbl] = 1

# Function to plot accuracies to bar chart
def plot_eval():
    plt.bar(*zip(*accuracy.items()))
    plt.ylabel('Accuracy')
    plt.title('OCR Evaluation')
    plt.show(block=True)

# Function to calculate accuracies
def calc_accuracy():
    for lbl in hits.keys():
        accuracy[lbl] = hits[lbl] / totals[lbl]
    accuracy["EFFECTIVE"] = (accuracy["GRADE"] + accuracy["COURSE"] + accuracy["CREDIT"] + accuracy["LEVEL"]) / 4
    accuracy["AGGREGATE"] = (accuracy["GRADE"] + accuracy["COURSE"] + accuracy["CREDIT"] + accuracy["LEVEL"] + accuracy["EXTRA"]) / 5

# Function to print dictionaries
def print_dicts():
    print("~~~~~Hits~~~~~")
    for lbl in hits.keys():
        print(str(lbl) + " : " + str(hits[lbl]))
    print("~~~~~Totals~~~~~")
    for tot in totals.keys():
        print(str(tot) + " : " + str(totals[tot]))
    print("~~~~~Accuracies~~~~~")
    for lbl in accuracy.keys():
        print(str(lbl) + " : " + str(accuracy[lbl]))

if __name__ == "__main__":
    eval()
