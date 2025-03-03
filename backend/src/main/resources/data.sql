-- Insert DocumentType
INSERT INTO document_type (id,  name, updated_at, created_at, prompt)
VALUES ('4b7e2f6e-8f6d-4d2b-9f4a-3e2d7b6e8f6d', 'Identity', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
'Extract the following information from the ID document in JSON format: { "name": "string", "surname": "string", "idNumber": "string", "birthDate": "string" }');

INSERT INTO document_type (id, name, updated_at, created_at, prompt)
VALUES ('5b7e2f6e-8f6d-4d2b-9f4a-3e2d7b6e8f65', 'Invoice', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
'have the result of an OCR reading of a paper invoice.
The OCR result is disorganized.
Your task is to organize the information and structure it into a JSON with a specific structure.
The response should be as concise as possible.
Your task is to generate a JSON with the data for a given OCR output.
Do not group invoice lines.
Write the full json.

Output format:
{
    "SenderName": "",
    "RecipientName": "",
    "RecipientAddress": "",
    "ExpeditionDate": "0001-01-01T00:00:00",
    "InvoiceNumber": "",
    "LicensePlate": "",
    "ShippingName": "",
    "TotalPallets": 0,
    "TotalPackages": 0,
    "GrossWeight": 0,
    "NetWeight": 0,
    "TotalAmount": 0,
    "Currency": "EUR",
    "IncotermName": "",
    "NumberCmr": "",
    "RecycledPlasticNetWeight": 0,
    "NonRecycledPlasticNetWeight": 0,
    "InvoiceLines": [
        {
            "Product": "",
            "NumPallets": 0,
            "TypeEmbalage": "",
            "NumPackages": 0,
            "GrossWeight": 0,
            "NetWeight": 0,
            "RecycledPlasticNetWeight": 0,
            "NonRecycledPlasticNetWeight": 0,
            "Price": 0,
            "TotalAmount": 0
        }
    ]
}');


INSERT INTO document_type (id, name, updated_at, created_at, prompt)
VALUES ('6b7e2f6e-8f6d-4d2b-9f4a-3e2d7b6e8f66', 'CMR',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
'I have the result of an OCR reading of a CMR (Convention relative au contract de transport international de Marchandise par Route) document.
The OCR result unstructured and has irrelevant information.
Your task is to extract some information and build a JSON with a specific structure.
Your response must be a json.
The response should be as concise as possible.
Do not include any introductory statements or clarifications in the output.

Output format:
{
    ""CarrierName"": """",
    ""LicensePlateInCarrierObservations"": """",
    ""NumberCMR"": """"
}');
