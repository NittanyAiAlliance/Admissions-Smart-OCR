from flask import Flask, json
import json

api = Flask(__name__)

"""
@api {post} /req Receive request for available transcripts
@apiName AvailableTranscriptRequest
"""


@api.route('/req', methods=['GET'])
def available_transcript_request():
    available_transcripts = {
        "count": 2,
        "transcripts": [
            {
                "PSU_ID": "975286999",
                "EXT_ORG_ID": "100068749",
                "DOCUMENT_ID": "65465413216",
                "NAME": {
                    "FIRST": "MARVIN",
                    "MIDDLE": "LEWIS",
                    "LAST": "JONES JR"
                },
                "CAMPUS": "UNIVERSITY PARK",
                "CITIZENSHIP": {
                    "RES_CODE": "USA",
                    "RES_NAME": "UNITED STATES OF AMERICA",
                    "RES_STATUS": "UNITED STATES CITIZEN",
                    "HS_CODE": "USA",
                    "HS_NAME": "UNITED STATES OF AMERICA"
                }
            },
            {
                "PSU_ID": "972987444",
                "EXT_ORG_ID": "100070715",
                "DOCUMENT_ID": "65458621216",
                "NAME": {
                    "FIRST": "JARED",
                    "MIDDLE": "THOMAS",
                    "LAST": "GOFF"
                },
                "CAMPUS": "BEAVER",
                "CITIZENSHIP": {
                    "RES_CODE": "USA",
                    "RES_NAME": "UNITED STATES OF AMERICA",
                    "RES_STATUS": "UNITED STATES CITIZEN",
                    "HS_CODE": "USA",
                    "HS_NAME": "UNITED STATES OF AMERICA"
                }
            }
        ]
    }
    return json.dumps(available_transcripts)


if __name__ == "__main__":
    api.run()