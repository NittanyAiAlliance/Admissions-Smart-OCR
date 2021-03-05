# Utility script to generate testing/evaluation data given SpaCy tokens and assign entity labels

from spacy.tokenizer import Tokenizer
from spacy.lang.en import English
import numpy as np

# Generate NLP, load data and loop
nlp = English()
mode = 0 # Toggle between training=0 and eval=1
tfp = "./train_data.npy"
TRAIN_DATA = np.load(tfp, allow_pickle=True).tolist()
efp = "./eval_data.npy"
EVAL_DATA = np.load(efp, allow_pickle=True).tolist()

# Function to handle entering training data
def enter_training_data(text):
    doc = nlp(text)
    data_entry = (text, [])

    # Print all tokens in line
    for token in doc:
        print(str(token.i) + ": " + token.text)

    # Ask user to label all tokens
    token_index = 0
    while token_index < len(doc):
        this_end = int(input("Label from " + str(token_index) + " to : "))
        this_label = input("Label is : ")
        data_entry[1].append((token_index, this_end, this_label))
        token_index = this_end + 1

    TRAIN_DATA.append(data_entry)

# Function to handle entering eval data
def enter_eval_data(text):
    doc = nlp(text)
    data_entry = (text, [])

    # Print all tokens in line
    for token in doc:
        print(str(token.i) + ": " + token.text)

    # Ask user to label all tokens
    token_index = 0
    while token_index < len(doc):
        this_end = int(input("Label from " + str(token_index) + " to : "))
        this_label = input("Label is : ")
        data_entry[1].append([this_label, doc[token_index:this_end+1].text])
        token_index = this_end + 1

    EVAL_DATA.append(data_entry)

# Function to save data
def save():
    np.save(tfp, np.asarray(TRAIN_DATA))
    np.save(efp, np.asarray(EVAL_DATA))
    print("Saved training & evaluation data.")

# Display op menu
print("~~~~~~ Smart OCR Data Utility ~~~~~~")
print("done(): finish and save data")
print("dumpt(): to view training data as JSON")
print("dumpe(): to view evaluation data as JSON")
print("showt(): show training data with index")
print("showe(): show eval data with index")
print("deletet(): delete train data at index")
print("deletee(): delete eval data at index")
print("adde(): switch to entering eval data")
print("addt(): switch to entering training data")
print("save(): save data")

# Run loop
while 1==1:

    # Receive text line input, setup data entry
    if(mode == 0):
        text = input("Training Text: ")
    else:
        text = input("Eval Text: ")
    if(text == "done()"):
        break
    elif(text == "dumpt()"):
        print(json.dumps(TRAIN_DATA))
    elif(text == "dumpe()"):
        print(json.dumps(EVAL_DATA))
    elif(text == "showt()"):
        ind = 0
        for entry, index in TRAIN_DATA:
            print(str(ind) + ": " + str(entry) + " : " + str(index))
            ind += 1
    elif(text == "showe()"):
        ind = 0
        for entry, index in EVAL_DATA:
            print(str(ind) + ": " + str(entry) + " : " + str(index))
            ind += 1
    elif(text == "deletet()"):
        index = int(input("Delete at index: "))
        if index > 0 and index < len(TRAIN_DATA):
            TRAIN_DATA.pop(index)
    elif(text == "ext()"):
        for data in TRAIN_DATA:
            data[1] = [label for label in data[1] if not label[2]=="EXTRA"]
    elif(text == "deletee()"):
        index = int(input("Delete at index: "))
        if index > 0 and index < len(EVAL_DATA):
            EVAL_DATA.pop(index)
    elif(text == "adde()"):
        mode = 1
        print("Switched to evaluation data")
    elif(text == "addt()"):
        mode = 0
        print("Switched to training data")
    elif(text == "save()"):
        save()
    else:
        if(mode == 0):
            enter_training_data(text)
        else:
            enter_eval_data(text)

save()