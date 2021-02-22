# Utility script to generate testing/evaluation data given SpaCy tokens and assign entity labels

from spacy.tokenizer import Tokenizer
from spacy.lang.en import English
import numpy as np

# Generate NLP, load data and loop
nlp = English()
fp = "./train_data.npy"
TRAIN_DATA = np.load(fp)

print("Type done() when finished to save data")
while 1==1:

    # Receive text line input, setup data entry
    text = input("Text: ")
    if(text == "done()"):
        break
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
np.save(fp, TRAIN_DATA)
print("Saved train data. Exiting.")