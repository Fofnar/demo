from fastapi import FastAPI

from api.routes import router

app = FastAPI(
    title="Felyxor AI Service",
    description="AI service dedicated to sales analysis, anomalies, predictions and business recommendations.",
    version="1.0.0",
)

# Enregistre les routes principales du service IA.
app.include_router(router)


@app.get("/")
def root():
    """
    Public entry point for the AI ​​service.
    Used to quickly verify that the service is accessible.
    """
    return {
        "service": "Felyxor AI Service",
        "status": "running",
        "version": "1.0.0",
    }


@app.get("/health")
def health_check():
    """
    Health endpoint used by deployment platforms
    and availability tests.
    """
    return {
        "status": "healthy"
    }