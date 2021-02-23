# Utility script to generate testing/evaluation data given SpaCy tokens and assign entity labels

from spacy.tokenizer import Tokenizer
from spacy.lang.en import English
import numpy as np

# Generate NLP, load data and loop
nlp = English()
fp = "./train_data.npy"
TRAIN_DATA = np.load(fp, allow_pickle=True).tolist()

# Display op menu
print("~~~~~~ Smart OCR Data Utility ~~~~~~")
print("done(): finish and save data")
print("dump(): to view training data as JSON")
print("edit(): show training data with index")
print("delete(): delete data at index")

# Run loop
while 1==1:

    # Receive text line input, setup data entry
    text = input("Text: ")
    if(text == "done()"):
        break
    elif(text == "dump()"):
        print(json.dumps(TRAIN_DATA))
    elif(text == "edit()"):
        ind = 0
        for entry, index in TRAIN_DATA:
            print(str(ind) + ": " + str(entry) + " : " + str(index))
            ind += 1
    elif(text == "delete()"):
        index = input("Delete at index: ")
        if index > 0 and index < len(TRAIN_DATA):
            TRAIN_DATA.pop(index)
    else:
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

# Save to flat file
np.save(fp, np.asarray(TRAIN_DATA))
print("Saved train data. Exiting.")