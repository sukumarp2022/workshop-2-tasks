# Modernized Python Application

> This directory is where participants build the migrated Python application during Lab 4.

## Target Structure

```
src-modernized/
├── app/
│   ├── __init__.py
│   ├── main.py
│   ├── config.py
│   ├── database.py
│   ├── models/
│   ├── schemas/
│   ├── repositories/
│   ├── services/
│   └── routers/
├── tests/
├── data/
│   └── import_legacy_data.py
├── requirements.txt
├── .env.example
└── README.md
```

## Getting Started

After completing the migration:

```bash
cd src-modernized
pip install -r requirements.txt
uvicorn app.main:app --reload
```

Then visit: http://localhost:8000/docs for Swagger UI
