from fastapi import FastAPI, APIRouter
from pydantic import BaseModel
from typing import List
from api.ai_service import run_ai_analysis

#Création d'un routeur FastAPI
#Il permet de grouper plus endpoints API dans un même module
router = APIRouter()

#Modèle de données représentant une vente
#Pydantic va automatiquement verifier ques les données reçues correspondent à ce modèle
class SalesData(BaseModel):
    date: str       # date de la vente
    product: str    # nom du produit
    price: float    # prix du produit
    quantity: int   # quantité vendue
    stock: int      # stock restant du produit

# Modèle représentant la requête complète envoyée à l'API
# L'utilisateur doit envoyer une liste de ventes
class SalesRequest(BaseModel):
    data: List[SalesData]

# Création d'un endpoint POST accessible à l'URL :
@router.post("/api/analyze")
def analyze_sales(request: SalesRequest):

    # Conversion des objets Pydantic en dictionnaires Python
    data =[item.dict() for item in request.data]

    # Appel du moteur d'analyse IA
    result = run_ai_analysis(data)

    return result