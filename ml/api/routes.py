from typing import List

from fastapi import APIRouter
from pydantic import BaseModel, Field

from .ai_service import run_ai_analysis

# Routeur dédié aux endpoints d'analyse IA.
router = APIRouter()


class SalesData(BaseModel):
    """
    Représente une ligne de vente reçue par le service IA.
    """

    date: str = Field(..., description="Date of sale")
    product: str = Field(..., description="Product Name")
    price: float = Field(..., description="Unit price")
    quantity: int = Field(..., description="Quantité vendue")
    stock: int = Field(..., description="Remaining stock")


class SalesRequest(BaseModel):
    """
    Représente la charge utile complète envoyée par le backend Spring Boot.
    """

    data: List[SalesData]


@router.post("/api/analyze")
def analyze_sales(request: SalesRequest):
    """
    Executes the complete AI analytics pipeline on received sales.
    """
    data = [item.model_dump() for item in request.data]
    return run_ai_analysis(data)