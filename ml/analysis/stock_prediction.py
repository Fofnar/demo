import pandas as pd

#Prédire rupture
def predict_stockout(data):

    # Vérifier que des données ont été envoyées
    if not data:
        return {"error": "no sales data"}
    
    # Convertir les données en DataFrame pandas
    df = pd.DataFrame(data)

    # Convertir la colonne date (souvent reçue en string)
    df["date"] = pd.to_datetime(df["date"])

    # Liste qui va contenir les résultats pour chaque produit
    result = []

    #Récupérer la liste des produits uniques
    products = df["product"].unique()

    # Boucle sur chaque produit
    for product in products:

        # Filtrer les ventes de ce produit
        product_df = df[df["product"] == product]

        # Calculer la moyenne de ventes par jour
        avg_sales = product_df["quantity"].mean()

        # Récupérer le dernier stock enregistré
        # .iloc[-1] = dernière ligne
        current_stock = product_df["stock"].iloc[-1]

        # Si aucune vente, impossible d'estimer la rupture
        if avg_sales == 0:
            days_left = None 
        else:
            #Estimation du nombre de jour avant rupture
            days_left = current_stock / avg_sales

        #Ajouter le resultat pour ce produit
        result.append({
            "product": product,
            "current_stock": int(current_stock),
            "avg_daily_sales": float(avg_sales),
            "estimated_days_before_stockout": float(days_left) if days_left else None
        })
    #Retourner les predictions pour tous les produits
    return{
        "stock_prediction": result
    }

#recommander réapprovisionnement
def stock_recommendations(stock_predictions):

    #Liste pour les recommandations
    recommendations = []

    # Boucle sur chaque prediction de rupture
    for item in stock_predictions["stock_prediction"]:

        #Recuperer stimation du nombre de jour avant rupture
        days_left = item["estimated_days_before_stockout"]

        #Recuperer le nom du produit
        product = item["product"]

        #On continue si n'y pas de prediction
        if days_left is None:
            continue
        
        #Si le nombre de jour avant rupture inferieur à 3 -> stock critique
        if days_left < 3:
            recommendations.append(
                f"{product}: Critical stock. Restock imediately."
            )

        #Si le nombre de jour avant rupture inferieur à 7 -> stock faible
        elif days_left < 7:
            recommendations.append(
                f"{product}: Low stock. Plan restock soon."
            )

        #Sinon niveau de stock normal
        else:
            recommendations.append(
                f"{product}:  Stock level Healthy"
            )

    #Retourner les recommandations pour tous les produits      
    return{
        "stock_recommendations": recommendations
    }

# ============================= TEST ============================
if __name__ == "__main__":

    data = [
     {"date":"2025-01-01","product":"Laptop","price":1000,"quantity":1,"stock":100},
     {"date":"2025-01-02","product":"Mouse","price":20,"quantity":10,"stock":5},
     {"date":"2025-01-03","product":"Laptop","price":1000,"quantity":2,"stock":50},
     {"date":"2025-01-04","product":"Keyboard","price":50,"quantity":5,"stock":9},
     {"date":"2025-01-05","product":"Laptop","price":1000,"quantity":1,"stock":70}
    ]

    stock_prediction = predict_stockout(data)

    print(stock_prediction)
    print(stock_recommendations(stock_prediction))