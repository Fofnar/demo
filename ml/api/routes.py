from typing import List

from fastapi import APIRouter
from pydantic import BaseModel, Field

from .ai_service import run_ai_analysis


# Routeur FastAPI utilisé pour regrouper les endpoints IA
router = APIRouter()


class SalesData(BaseModel):
    """
    Représente une ligne de vente reçue par l'API.
    """
    date: str = Field(..., description="Date de la vente")
    product: str = Field(..., description="Nom du produit")
    price: float = Field(..., description="Prix unitaire")
    quantity: int = Field(..., description="Quantité vendue")
    stock: int = Field(..., description="Stock restant")


class SalesRequest(BaseModel):
    """
    Représente la requête complète envoyée à l'API.
    """
    data: List[SalesData]


@router.post("/api/analyze")
def analyze_sales(request: SalesRequest):
    """
    Lance l'analyse IA complète sur la liste des ventes reçues.
    """
    data = [item.dict() for item in request.data]
    result = run_ai_analysis(data)
    return result