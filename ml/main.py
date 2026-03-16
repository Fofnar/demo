from api.ai_service import run_ai_analysis
from fastapi import FastAPI
from api.routes import router

app = FastAPI(
    title= "AI Service",
    version= "1.0"
)

#Ajout du routeur à l'application
app.include_router(router)


#=============================== TEST du moteur d'analyse IA ===================================
data = [
 {"date":"2025-01-01","product":"Laptop","price":1000,"quantity":1, "stock":100},
 {"date":"2025-01-02","product":"Mouse","price":20,"quantity":10, "stock":5},
 {"date":"2025-01-03","product":"Laptop","price":1000,"quantity":2, "stock":50},
 {"date":"2025-01-04","product":"Keyboard","price":50,"quantity":5, "stock":9},
 {"date":"2025-01-05","product":"Laptop","price":1000,"quantity":1, "stock":70}
]

print(run_ai_analysis(data))
