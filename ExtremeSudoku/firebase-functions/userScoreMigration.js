/**
 * Firebase Cloud Functions for User Score Migration
 * 
 * Deploy: firebase deploy --only functions:migrateUserScores
 * 
 * This function initializes scoring fields for existing users
 */

const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Initialize Firebase Admin if not already initialized
if (!admin.apps.length) {
  admin.initializeApp();
}

const db = admin.firestore();

/**
 * Migrate all existing users to include scoring fields
 * 
 * Triggered manually or via scheduled function
 * Can be called via HTTP: POST https://YOUR-PROJECT.cloudfunctions.net/migrateUserScores
 */
exports.migrateUserScores = functions.https.onRequest(async (req, res) => {
  try {
    console.log('🚀 Starting user score migration...');
    
    // Get all users
    const usersSnapshot = await db.collection('users').get();
    
    if (usersSnapshot.empty) {
      console.log('⚠️ No users found to migrate');
      return res.status(200).json({ 
        success: true, 
        message: 'No users to migrate',
        migratedCount: 0 
      });
    }
    
    const batch = db.batch();
    let migratedCount = 0;
    let skippedCount = 0;
    
    // Process each user
    usersSnapshot.forEach((doc) => {
      const userData = doc.data();
      
      // Check if user already has scoring fields
      if (userData.totalScore !== undefined) {
        console.log(`⏭️ Skipping user ${doc.id} - already migrated`);
        skippedCount++;
        return;
      }
      
      // Initialize scoring fields with default values
      const scoringFields = {
        // Total scores
        totalScore: 0,
        offlineScore: 0,
        onlineScore: 0,
        
        // Rankings
        globalRank: null,
        countryRank: null,
        
        // ELO ratings
        eloRating: 1200, // Standard starting ELO
        liveBattleElo: 1200,
        blindRaceElo: 1200,
        
        // Progression
        level: 1,
        experience: 0,
        
        // Achievements
        unlockedBadges: [],
        
        // Timestamps
        lastScoreUpdate: admin.firestore.FieldValue.serverTimestamp(),
        scoreMigrationDate: admin.firestore.FieldValue.serverTimestamp()
      };
      
      // Update user document
      batch.update(doc.ref, scoringFields);
      migratedCount++;
      
      console.log(`✅ Migrated user ${doc.id}`);
    });
    
    // Commit batch write
    await batch.commit();
    
    console.log(`🎉 Migration completed! Migrated: ${migratedCount}, Skipped: ${skippedCount}`);
    
    return res.status(200).json({
      success: true,
      message: 'User score migration completed successfully',
      migratedCount: migratedCount,
      skippedCount: skippedCount,
      totalUsers: usersSnapshot.size
    });
    
  } catch (error) {
    console.error('❌ Migration failed:', error);
    return res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

/**
 * Migrate UserStats for all users
 * 
 * Adds detailed statistics fields to UserStats collection
 */
exports.migrateUserStats = functions.https.onRequest(async (req, res) => {
  try {
    console.log('🚀 Starting UserStats migration...');
    
    // Get all user stats
    const statsSnapshot = await db.collection('user_stats').get();
    
    if (statsSnapshot.empty) {
      console.log('⚠️ No user stats found to migrate');
      return res.status(200).json({ 
        success: true, 
        message: 'No stats to migrate',
        migratedCount: 0 
      });
    }
    
    const batch = db.batch();
    let migratedCount = 0;
    let skippedCount = 0;
    
    // Process each user stat
    statsSnapshot.forEach((doc) => {
      const statsData = doc.data();
      
      // Check if already migrated
      if (statsData.totalScore !== undefined) {
        console.log(`⏭️ Skipping stats for ${doc.id} - already migrated`);
        skippedCount++;
        return;
      }
      
      // Initialize detailed stats fields
      const detailedStats = {
        // Score statistics
        totalScore: 0,
        highestScore: 0,
        averageScore: 0,
        
        // Move statistics
        correctMoves: 0,
        wrongMoves: 0,
        totalMoves: statsData.totalMoves || 0,
        accuracy: 0.0,
        
        // Streak statistics
        maxStreakInGame: 0,
        maxStreakAllTime: 0,
        totalStreaks: 0,
        
        // Bonus statistics
        perfectGames: 0,
        fastCompletions: 0,
        boxCompletions: 0,
        rowCompletions: 0,
        columnCompletions: 0,
        
        // Time statistics
        fastestEasyTime: null,
        fastestMediumTime: null,
        fastestHardTime: null,
        fastestExpertTime: null,
        
        // Difficulty breakdowns
        easyStats: {
          gamesPlayed: 0,
          gamesWon: 0,
          totalScore: 0,
          bestScore: 0,
          averageTime: 0
        },
        mediumStats: {
          gamesPlayed: 0,
          gamesWon: 0,
          totalScore: 0,
          bestScore: 0,
          averageTime: 0
        },
        hardStats: {
          gamesPlayed: 0,
          gamesWon: 0,
          totalScore: 0,
          bestScore: 0,
          averageTime: 0
        },
        expertStats: {
          gamesPlayed: 0,
          gamesWon: 0,
          totalScore: 0,
          bestScore: 0,
          averageTime: 0
        },
        
        // Migration metadata
        statsMigrationDate: admin.firestore.FieldValue.serverTimestamp()
      };
      
      // Update stats document
      batch.update(doc.ref, detailedStats);
      migratedCount++;
      
      console.log(`✅ Migrated stats for ${doc.id}`);
    });
    
    // Commit batch write
    await batch.commit();
    
    console.log(`🎉 Stats migration completed! Migrated: ${migratedCount}, Skipped: ${skippedCount}`);
    
    return res.status(200).json({
      success: true,
      message: 'UserStats migration completed successfully',
      migratedCount: migratedCount,
      skippedCount: skippedCount,
      totalStats: statsSnapshot.size
    });
    
  } catch (error) {
    console.error('❌ Stats migration failed:', error);
    return res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

/**
 * Scheduled migration runner - runs daily to catch new users
 * Scheduled to run every day at 3 AM UTC
 * 
 * Deploy: firebase deploy --only functions:scheduledScoreMigration
 */
exports.scheduledScoreMigration = functions.pubsub
  .schedule('0 3 * * *')
  .timeZone('UTC')
  .onRun(async (context) => {
    console.log('⏰ Running scheduled score migration...');
    
    try {
      // Get users created in the last 2 days who don't have scoring fields
      const twoDaysAgo = new Date();
      twoDaysAgo.setDate(twoDaysAgo.getDate() - 2);
      
      const usersSnapshot = await db.collection('users')
        .where('createdAt', '>=', twoDaysAgo)
        .get();
      
      if (usersSnapshot.empty) {
        console.log('✅ No new users to migrate');
        return null;
      }
      
      const batch = db.batch();
      let count = 0;
      
      usersSnapshot.forEach((doc) => {
        const userData = doc.data();
        
        // Skip if already has scoring fields
        if (userData.totalScore !== undefined) {
          return;
        }
        
        batch.update(doc.ref, {
          totalScore: 0,
          offlineScore: 0,
          onlineScore: 0,
          globalRank: null,
          countryRank: null,
          eloRating: 1200,
          liveBattleElo: 1200,
          blindRaceElo: 1200,
          level: 1,
          experience: 0,
          unlockedBadges: [],
          lastScoreUpdate: admin.firestore.FieldValue.serverTimestamp(),
          scoreMigrationDate: admin.firestore.FieldValue.serverTimestamp()
        });
        
        count++;
      });
      
      if (count > 0) {
        await batch.commit();
        console.log(`✅ Scheduled migration completed: ${count} users migrated`);
      }
      
      return null;
      
    } catch (error) {
      console.error('❌ Scheduled migration failed:', error);
      throw error;
    }
  });

/**
 * Trigger migration for a specific user
 * 
 * Can be called from client: functions.httpsCallable('migrateUserScore')({ userId: 'xxx' })
 */
exports.migrateUserScore = functions.https.onCall(async (data, context) => {
  // Verify authentication
  if (!context.auth) {
    throw new functions.https.HttpsError(
      'unauthenticated',
      'User must be authenticated to migrate score'
    );
  }
  
  const userId = data.userId || context.auth.uid;
  
  try {
    console.log(`🔄 Migrating score for user: ${userId}`);
    
    const userRef = db.collection('users').doc(userId);
    const userDoc = await userRef.get();
    
    if (!userDoc.exists) {
      throw new functions.https.HttpsError(
        'not-found',
        'User not found'
      );
    }
    
    const userData = userDoc.data();
    
    // Check if already migrated
    if (userData.totalScore !== undefined) {
      return {
        success: true,
        message: 'User already migrated',
        alreadyMigrated: true
      };
    }
    
    // Initialize scoring fields
    await userRef.update({
      totalScore: 0,
      offlineScore: 0,
      onlineScore: 0,
      globalRank: null,
      countryRank: null,
      eloRating: 1200,
      liveBattleElo: 1200,
      blindRaceElo: 1200,
      level: 1,
      experience: 0,
      unlockedBadges: [],
      lastScoreUpdate: admin.firestore.FieldValue.serverTimestamp(),
      scoreMigrationDate: admin.firestore.FieldValue.serverTimestamp()
    });
    
    console.log(`✅ Successfully migrated user: ${userId}`);
    
    return {
      success: true,
      message: 'User score migrated successfully',
      userId: userId
    };
    
  } catch (error) {
    console.error(`❌ Failed to migrate user ${userId}:`, error);
    throw new functions.https.HttpsError(
      'internal',
      error.message
    );
  }
});
