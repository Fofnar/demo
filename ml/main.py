from fastapi import FastAPI

from api.routes import router
from api.ai_service import run_ai_analysis


app = FastAPI(
    title="AI Service",
    version="1.0",
)

# Ajout du routeur principal à l'application
app.include_router(router)


def get_demo_data():
    """
    Fournit un jeu de données de démonstration suffisamment riche pour tester
    les ventes, les anomalies, la prédiction, le stock et la santé business.
    """
    return [
        # Phase stable
        {"date": "2025-01-01", "product": "Laptop", "price": 1000, "quantity": 3, "stock": 120},
        {"date": "2025-01-01", "product": "Mouse", "price": 20, "quantity": 15, "stock": 80},
        {"date": "2025-01-01", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 60},

        {"date": "2025-01-02", "product": "Laptop", "price": 1000, "quantity": 4, "stock": 116},
        {"date": "2025-01-02", "product": "Mouse", "price": 20, "quantity": 14, "stock": 76},
        {"date": "2025-01-02", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 56},

        {"date": "2025-01-03", "product": "Laptop", "price": 1000, "quantity": 4, "stock": 112},
        {"date": "2025-01-03", "product": "Mouse", "price": 20, "quantity": 16, "stock": 72},
        {"date": "2025-01-03", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 52},

        {"date": "2025-01-04", "product": "Laptop", "price": 1000, "quantity": 5, "stock": 107},
        {"date": "2025-01-04", "product": "Mouse", "price": 20, "quantity": 15, "stock": 68},
        {"date": "2025-01-04", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 48},

        {"date": "2025-01-05", "product": "Laptop", "price": 1000, "quantity": 4, "stock": 103},
        {"date": "2025-01-05", "product": "Mouse", "price": 20, "quantity": 15, "stock": 64},
        {"date": "2025-01-05", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 44},

        # Accélération de la demande
        {"date": "2025-01-06", "product": "Laptop", "price": 1000, "quantity": 9, "stock": 94},
        {"date": "2025-01-06", "product": "Mouse", "price": 20, "quantity": 16, "stock": 60},
        {"date": "2025-01-06", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 40},

        {"date": "2025-01-07", "product": "Laptop", "price": 1000, "quantity": 10, "stock": 84},
        {"date": "2025-01-07", "product": "Mouse", "price": 20, "quantity": 14, "stock": 56},
        {"date": "2025-01-07", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 36},

        {"date": "2025-01-08", "product": "Laptop", "price": 1000, "quantity": 11, "stock": 73},
        {"date": "2025-01-08", "product": "Mouse", "price": 20, "quantity": 15, "stock": 52},
        {"date": "2025-01-08", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 32},

        # Anomalie basse
        {"date": "2025-01-09", "product": "Laptop", "price": 1000, "quantity": 5, "stock": 68},
        {"date": "2025-01-09", "product": "Mouse", "price": 20, "quantity": 1, "stock": 6},
        {"date": "2025-01-09", "product": "Keyboard", "price": 50, "quantity": 1, "stock": 7},

        {"date": "2025-01-10", "product": "Laptop", "price": 1000, "quantity": 6, "stock": 62},
        {"date": "2025-01-10", "product": "Mouse", "price": 20, "quantity": 2, "stock": 5},
        {"date": "2025-01-10", "product": "Keyboard", "price": 50, "quantity": 1, "stock": 6},

        # Anomalie haute
        {"date": "2025-01-11", "product": "Laptop", "price": 1000, "quantity": 18, "stock": 44},
        {"date": "2025-01-11", "product": "Mouse", "price": 20, "quantity": 22, "stock": 30},
        {"date": "2025-01-11", "product": "Keyboard", "price": 50, "quantity": 14, "stock": 18},

        {"date": "2025-01-12", "product": "Laptop", "price": 1000, "quantity": 2, "stock": 42},
        {"date": "2025-01-12", "product": "Mouse", "price": 20, "quantity": 15, "stock": 25},
        {"date": "2025-01-12", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 12},

        # Retour à un rythme plus normal
        {"date": "2025-01-13", "product": "Laptop", "price": 1000, "quantity": 4, "stock": 38},
        {"date": "2025-01-13", "product": "Mouse", "price": 20, "quantity": 14, "stock": 21},
        {"date": "2025-01-13", "product": "Keyboard", "price": 50, "quantity": 8, "stock": 8},

        {"date": "2025-01-14", "product": "Laptop", "price": 1000, "quantity": 5, "stock": 33},
        {"date": "2025-01-14", "product": "Mouse", "price": 20, "quantity": 13, "stock": 18},
        {"date": "2025-01-14", "product": "Keyboard", "price": 50, "quantity": 7, "stock": 1},
    ]


if __name__ == "__main__":
    # Exécution locale du moteur d'analyse IA
    demo_data = get_demo_data()
    print(run_ai_analysis(demo_data))