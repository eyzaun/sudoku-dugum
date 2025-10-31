"""
Hızlı Firebase Yükleme - Source Field'lı
"""

import firebase_admin
from firebase_admin import credentials, firestore
from google.cloud.firestore_v1.base_query import FieldFilter
import csv

# Firebase initialize
cred = credentials.Certificate("sudoku-lonca-firebase-adminsdk-fbsvc-6a98b12b77.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

def get_difficulty(rating):
    """Rating'e göre zorluk belirle (tdoku backtrack count)"""
    if rating <= 5:
        return "easy"
    elif rating <= 20:
        return "medium"
    elif rating <= 50:
        return "hard"
    else:
        return "expert"

# Mevcut sayıları kontrol et
print("📊 Firebase'deki mevcut puzzle sayıları:")
for diff in ["easy", "medium", "hard", "expert"]:
    count = sum(1 for _ in db.collection('sudokus').where(filter=FieldFilter('difficulty', '==', diff)).limit(1000).stream())
    print(f"  {diff}: {count}")

print("\n🚀 500'er puzzle yükleniyor...")

difficulty_counts = {"easy": 0, "medium": 0, "hard": 0, "expert": 0}
batch = db.batch()
batch_count = 0
total = 0

with open('sudoku_dataset.csv', 'r') as file:
    reader = csv.DictReader(file)
    
    for row in reader:
        rating = int(row['rating'])
        difficulty = get_difficulty(rating)
        
        if difficulty_counts[difficulty] >= 500:
            continue
        
        doc_ref = db.collection('sudokus').document()
        batch.set(doc_ref, {
            'puzzle': row['question'],
            'solution': row['answer'],
            'difficulty': difficulty,
            'rating': rating,
            'source': row['source'],  # ← Source field eklendi
            'isXSudoku': False,
            'category': 'classic',
            'createdAt': firestore.SERVER_TIMESTAMP
        })
        
        batch_count += 1
        difficulty_counts[difficulty] += 1
        total += 1
        
        if batch_count >= 500:
            print(f"  Batch commit... ({total} puzzle)")
            batch.commit()
            batch = db.batch()
            batch_count = 0
        
        if all(c >= 500 for c in difficulty_counts.values()):
            break

if batch_count > 0:
    print(f"  Son batch commit... ({total} puzzle)")
    batch.commit()

print("\n✅ Yükleme tamamlandı!")
print(f"Toplam yüklenen: {total}")
for diff, count in difficulty_counts.items():
    print(f"  {diff}: {count}")
