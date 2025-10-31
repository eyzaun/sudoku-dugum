"""
Firebase'e Sudoku Yükleme Script'i
HuggingFace dataset'inden Firebase Firestore'a sudoku aktarımı
"""

import firebase_admin
from firebase_admin import credentials, firestore
from google.cloud.firestore_v1.base_query import FieldFilter
import csv
import os
from datetime import datetime

# Firebase Admin SDK initialize
# Service account key otomatik bulunacak
cred = credentials.Certificate("sudoku-lonca-firebase-adminsdk-fbsvc-6a98b12b77.json")
firebase_admin.initialize_app(cred)

db = firestore.client()

def get_difficulty(rating):
    """
    Rating'e göre zorluk seviyesi belirle
    Rating = tdoku solver backtrack sayısı (yüksek = zor)
    
    Dataset dağılımı:
    - %25 dilim: rating <= 3 (kolay puzzles)
    - %50 dilim: rating <= 17 (orta puzzles)
    - %75 dilim: rating <= 34 (zor puzzles)
    - %95+ dilim: rating > 50 (extreme puzzles)
    """
    if rating <= 5:
        return "easy"        # 0-5 backtrack: Kolay (mantıksal çözüm)
    elif rating <= 20:
        return "medium"      # 6-20 backtrack: Orta (biraz deneme-yanılma)
    elif rating <= 50:
        return "hard"        # 21-50 backtrack: Zor (çok backtrack)
    else:
        return "expert"      # 51+ backtrack: Expert (extreme backtrack)

def upload_sudokus_from_csv(csv_path, limit_per_difficulty=100):
    """
    CSV'den sudoku'ları okuyup Firebase'e yükle
    Her zorluk seviyesinden belirli sayıda yükler
    """
    
    # Önce Firebase'deki mevcut puzzle sayılarını kontrol et
    print("Firebase'deki mevcut puzzle sayıları kontrol ediliyor...")
    existing_counts = {
        "easy": 0,
        "medium": 0,
        "hard": 0,
        "expert": 0
    }
    
    for difficulty in existing_counts.keys():
        query = db.collection('sudokus').where(filter=FieldFilter('difficulty', '==', difficulty)).limit(10000).stream()
        existing_counts[difficulty] = sum(1 for _ in query)
        print(f"  {difficulty}: {existing_counts[difficulty]} mevcut")
    
    print()
    
    difficulty_counts = {
        "easy": existing_counts["easy"],
        "medium": existing_counts["medium"],
        "hard": existing_counts["hard"],
        "expert": existing_counts["expert"]
    }
    
    total_uploaded = 0
    batch = db.batch()
    batch_count = 0
    
    print(f"CSV dosyası okunuyor: {csv_path}")
    
    with open(csv_path, 'r') as file:
        reader = csv.DictReader(file)
        
        for row in reader:
            puzzle = row['question']
            solution = row['answer']
            rating = int(row['rating'])
            source = row['source']  # Dataset'teki source field'ı
            
            difficulty = get_difficulty(rating)
            
            # Her zorluktan limit kadar yükle
            if difficulty_counts[difficulty] >= limit_per_difficulty:
                continue
            
            # Sudoku document oluştur
            doc_ref = db.collection('sudokus').document()
            sudoku_data = {
                'puzzle': puzzle,
                'solution': solution,
                'difficulty': difficulty,
                'rating': rating,
                'source': source,  # Dataset source'u kaydet
                'isXSudoku': False,
                'category': 'classic',
                'createdAt': firestore.SERVER_TIMESTAMP
            }
            
            batch.set(doc_ref, sudoku_data)
            batch_count += 1
            difficulty_counts[difficulty] += 1
            total_uploaded += 1
            
            # Firebase batch limiti 500
            if batch_count >= 500:
                print(f"Batch commit ediliyor... ({total_uploaded} sudoku)")
                batch.commit()
                batch = db.batch()
                batch_count = 0
            
            # Tüm zorluklar doldu mu?
            if all(count >= limit_per_difficulty for count in difficulty_counts.values()):
                break
        
        # Kalan batch'i commit et
        if batch_count > 0:
            print(f"Son batch commit ediliyor... ({total_uploaded} sudoku)")
            batch.commit()
    
    print("\n✅ Yükleme tamamlandı!")
    print(f"Bu seferde yüklenen: {total_uploaded} sudoku")
    print("\nFirebase'deki toplam:")
    for diff, count in difficulty_counts.items():
        print(f"  {diff}: {count} sudoku")

def add_sample_x_sudoku():
    """X-Sudoku örnekleri ekle"""
    
    # X-Sudoku test puzzle'ları
    x_sudokus = [
        {
            'puzzle': '000000000000000000000000000000000000000000000000000000000000000000000000000000000',
            'solution': '534678912672195348198342567859761423426853791713924856961537284287419635345286179',
            'difficulty': 'hard',
            'rating': 45,
            'isXSudoku': True,
            'category': 'x-sudoku'
        }
    ]
    
    for sudoku in x_sudokus:
        sudoku['createdAt'] = firestore.SERVER_TIMESTAMP
        db.collection('sudokus').add(sudoku)
    
    print(f"✅ {len(x_sudokus)} X-Sudoku eklendi")

if __name__ == "__main__":
    print("=" * 60)
    print("Firebase Sudoku Yükleme Script'i")
    print("=" * 60)
    
    # HuggingFace dataset'inin yolu
    csv_path = "sudoku_dataset.csv"
    
    if not os.path.exists(csv_path):
        print(f"❌ Hata: {csv_path} bulunamadı!")
        print("\nÖnce dataset'i indirin:")
        print("  curl -L 'https://huggingface.co/datasets/sapientinc/sudoku-extreme/resolve/main/train.csv' -o sudoku_dataset.csv")
        exit(1)
    
    # Her zorluktan kaç tane yüklemek istediğinizi belirtin
    limit = int(input("\nHer zorluk seviyesinden TOPLAM kaç puzzle olsun? (şu anki + yeni): ") or "500")
    
    print(f"\nHer zorluk seviyesinden toplam {limit} puzzle olacak şekilde eksikler yüklenecek.")
    
    confirm = input("\nDevam etmek istiyor musunuz? (e/h): ")
    if confirm.lower() != 'e':
        print("İptal edildi.")
        exit(0)
    
    # Sudoku'ları yükle
    upload_sudokus_from_csv(csv_path, limit_per_difficulty=limit)
    
    # X-Sudoku örnekleri ekle
    add_x = input("\nX-Sudoku örnekleri eklensin mi? (e/h): ")
    if add_x.lower() == 'e':
        add_sample_x_sudoku()
    
    print("\n🎉 İşlem tamamlandı!")
    print("\nFirebase Console'dan kontrol edebilirsiniz:")
    print("  https://console.firebase.google.com/")
