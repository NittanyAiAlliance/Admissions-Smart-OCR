import numpy as np
from ocr_model import process_ocr
from tqdm.auto import tqdm
import matplotlib
import matplotlib.pyplot as plt
from difflib import SequenceMatcher
from stars import process_stars

# Load data
fp = "./eval_data.npy"
EVAL_DATA = np.load(fp, allow_pickle=True).tolist()

# Setup metrics
totals = {"GRADE": 0, "COURSE": 0, "LEVEL":0, "CREDIT":0, "EXTRA": 0, "COURSE_CODE": 0}
hits = {"GRADE": 0, "COURSE": 0, "LEVEL":0, "CREDIT":0, "EXTRA": 0, "COURSE_CODE": 0}
accuracy = {"GRADE": 0, "COURSE": 0, "LEVEL":0, "CREDIT":0, "EXTRA": 0, "COURSE_CODE": 0}
proximity = {"GRADE": 0, "COURSE": 0, "LEVEL":0, "CREDIT":0, "EXTRA": 0}
synopsis = 0

# Function to find similarity between two strings
def similar(a, b):
    return SequenceMatcher(None, a, b).ratio()

# Function to evaluate model accuracy against evaluation set
def eval():

    # Iterate and process
    for data in tqdm(EVAL_DATA):
        line = process_stars(process_ocr(data[0])) # Run OCR on text
        if(len(line) > 0):
            line = line["Line 0"]
        else:
            line = ''
        exp = data[1] # Save expected array of label pairs
        for label in exp:
            this_label = label[0]
            this_text = label[1]
            # Handle aggregate accuracy
            if(this_label != 'EXTRA' and this_label != 'COURSE_SUBJECT' and this_label != 'COURSE_NAME'):
                hit = False
                for entry in range(1, len(line)): # Iterate returned labels
                    if(line[entry][0] == this_label and line[entry][1] == this_text): # Return hit on exact match
                        inc_hits(this_label)
                        hit = True
                    elif(line[entry][0] == this_label and this_label == 'CREDIT'): # Handle credit value equivalence
                        try:
                            if(float(line[entry][1]) == float(this_text)):
                                inc_hits(this_label)
                                hit = True
                        except ValueError:
                            pass
                    elif(line[entry][0] == this_label): # Check similarity on other match types
                        add_prox(this_label, similar(line[entry][1], this_text))
                if(not hit):
                    inc_total(this_label)
            # Handle effective accuracy
            elif(this_label == 'EXTRA'):
                misfire = False
                for entry in range(1, len(line)): # Iterate returned labels
                    if(this_text in line[entry][1]):
                        if(line[entry][0] != 'EXTRA'):
                            inc_total(this_label)
                            misfire = True
                if(not misfire):
                    inc_hits('EXTRA')

    calc_accuracy()
    print_dicts()
    plot_eval()

# Function to increment hits
def inc_hits(lbl):
    if lbl in hits.keys():
        hits[lbl] = hits[lbl] + 1
        proximity[lbl] = ((proximity[lbl] * totals[lbl]) + 1) / (totals[lbl] + 1)
    else:
        hits[lbl] = 1
        proximity[lbl] = 1
    inc_total(lbl)

# Function to increment total
def inc_total(lbl):
    if lbl in totals.keys():
        totals[lbl] = totals[lbl] + 1
    else:
        totals[lbl] = 1

# Add a similarity value to the proximity
def add_prox(lbl, value):
    if lbl in proximity.keys():
        proximity[lbl] = ((proximity[lbl] * totals[lbl]) + value) / (totals[lbl] + 1)
    else:
        proximity[lbl] = 1

# Function to plot accuracies to bar chart
def plot_eval():
    plt.bar(list(accuracy.keys()) + [x + " Prox" for x in list(proximity.keys())] + ['Synopsis'], list(accuracy.values()) + list(proximity.values()) + [synopsis])
    plt.ylabel('Accuracy')
    plt.title('OCR Evaluation')
    plt.show(block=True)

# Function to calculate accuracies
def calc_accuracy():
    global synopsis
    for lbl in hits.keys():
        print("Loading " + str(lbl))
        accuracy[lbl] = hits[lbl] / totals[lbl]
    accuracy["EFFECTIVE"] = (accuracy["GRADE"] + accuracy["COURSE"] + accuracy["CREDIT"] + accuracy["LEVEL"]) / 4
    accuracy["AGGREGATE"] = (accuracy["GRADE"] + accuracy["COURSE"] + accuracy["CREDIT"] + accuracy["LEVEL"] + accuracy["EXTRA"]) / 5
    synopsis = (accuracy["GRADE"] + accuracy["CREDIT"] + accuracy["LEVEL"] + proximity["COURSE"]) / 4

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
    print("~~~~~Proximities~~~~~")
    for lbl in proximity.keys():
        print(str(lbl) + " : " + str(proximity[lbl]))
    print("~~~~~Synopsis~~~~~")
    print("Effective Synopsis : " + str(synopsis))

if __name__ == "__main__":
    eval()
