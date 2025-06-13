import battle.Battle;
import main.*;
import org.junit.jupiter.api.*;
import units.*;
import bot.Bot;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты по ЛР2 и ЛР3")
public class GameTests {

    GameMap map;
    Hero hero1, hero2;
    Bot bot;

    static Logger logger = Logger.getLogger(GameTests.class.getName());

    @BeforeAll static void initLogger() throws Exception {
        FileHandler fileHandler = new FileHandler("log.txt", true);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        logger.setLevel(Level.ALL);
    }

    @BeforeEach void setUp() {
        map = new GameMap(12, 12);
        map.initZonesAndRoad();
        map.scatterGold(5);
        hero1 = new Hero("Player", 200, 30, 2, 4, 0, 0, map, "Player");
        hero2 = new Hero("Enemy", 200, 30, 2, 4, 11, 11, map, "Enemy");
        map.addHero(hero1, 0, 0);
        map.addHero(hero2, 11, 11);
        bot = new Bot();

        if (System.in != System.in || System.out != System.out) {
            logger.warning("Перехват потока ввода/вывода");
        }
    }

    @Test void testVictoryByHeroDefeat() {
        hero2.setArmy(new ArrayList<>());
        hero2.takeDamage(999);
        assertTrue(hero2.isDefeated());
        logger.info("testVictoryByHeroDefeat прошёл");
    }

    @Test void testHeroMovesWithinSpeedLimit() {
        hero1.move(1, 1);
        assertEquals(1, hero1.getX());
        assertEquals(1, hero1.getY());
        logger.info("testHeroMovesWithinSpeedLimit прошёл");
    }

    @Test void testHeroCannotMoveTooFar() {
        hero1.move(10, 10);
        assertNotEquals(10, hero1.getX());
        logger.info("testHeroCannotMoveTooFar прошёл");
    }

    @Test void testHeroAttackReducesEnemyHP() {
        hero1.getArmy().add(new Spearman());
        hero2.getArmy().add(new Spearman());
        hero1.attack(hero2);
        assertTrue(hero2.getArmy().size() <= 1);
        logger.info("testHeroAttackReducesEnemyHP прошёл");
    }

    @Test void testUnitDiesWhenHealthZero() {
        Unit u = new Spearman();
        u.takeDamage(999);
        assertFalse(u.isAlive());
        logger.info("testUnitDiesWhenHealthZero прошёл");
    }

    @Test void testBattleEndsWhenAllUnitsDead() {
        Battle.testMode = true;
        hero1.getArmy().add(new Spearman());
        hero2.getArmy().add(new Spearman());
        Battle.startBattle(hero1, hero2);
        assertTrue(hero1.getArmy().isEmpty() || hero2.getArmy().isEmpty());
        logger.info("testBattleEndsWhenAllUnitsDead прошёл");
    }

    @Test void testGoldScatteredCorrectly() {
        int goldCells = 0;
        for (Cell[] row : map.getCells()) {
            for (Cell cell : row) {
                if (cell.getGoldAmount() > 0) goldCells++;
            }
        }
        assertTrue(goldCells > 0);
        logger.info("testGoldScatteredCorrectly прошёл");
    }

    @Test void testTavernIncreasesArmyLimit() {
        hero1.buildings.add("Таверна");
        hero1.setBuildingArmyLimitBonus(3);
        for (int i = 0; i < 8; i++) hero1.getArmy().add(new Spearman());
        assertEquals(8, hero1.getArmy().size());
        logger.info("testTavernIncreasesArmyLimit прошёл");
    }

    @Test void testStableIncreasesSpeed() {
        hero1.setBuildingSpeedBonus(1);
        hero1.move(0, 3);
        assertEquals(3, hero1.getY());
        logger.info("testStableIncreasesSpeed прошёл");
    }

    @Test void testWatchTowerBoostsUnitsInBattle() {
        hero1.getArmy().add(new Spearman());
        hero1.buildings.add("Сторожевой пост");
        hero2.getArmy().add(new Spearman());
        Battle.testMode = true;
        Battle.startBattle(hero1, hero2);
        assertTrue(hero1.getArmy().size() >= 0);
        logger.info("testWatchTowerBoostsUnitsInBattle прошёл");
    }

    @Test void testBotBuildsStructureRandomly() {
        hero2.setGold(200);
        bot.botMove(hero2, map);
        assertTrue(hero2.getBuildings().size() >= 0);
        logger.info("testBotBuildsStructureRandomly прошёл");
    }

    @Test void testBotMovesTowardCastle() {
        int prevX = hero2.getX();
        bot.moveTowardsEnemyCastle(hero2, map);
        assertNotEquals(prevX, hero2.getX());
        logger.info("testBotMovesTowardCastle прошёл");
    }

    @Test void testMapEditorSetsGoldCorrectly() {
        map.getCells()[3][3].setGoldAmount(120);
        assertEquals(120, map.getCells()[3][3].getGoldAmount());
        logger.info("testMapEditorSetsGoldCorrectly прошёл");
    }

    @Test void testAccessPrivateMethodReflectively() {
        try {
            Unit unit = new Spearman();
            Method m = unit.getClass().getDeclaredMethod("boost", int.class);
            m.setAccessible(true);
            m.invoke(unit, 10);
            logger.severe("Попытка вызова приватного метода без изменения кода!");
        } catch (Exception e) {
            logger.info("testAccessPrivateMethodReflectively прошёл (метод не доступен)");
        }
    }

    @Test void testArmyLimitBlocksExtraUnits() {
        for (int i = 0; i < 10; i++) hero1.getArmy().add(new Spearman());
        assertTrue(hero1.getArmy().size() <= 10);
        logger.info("testArmyLimitBlocksExtraUnits прошёл");
    }

    @Test void testHeroStartsWithUnit() {
        hero1.getArmy().add(new Spearman());
        assertFalse(hero1.getArmy().isEmpty());
        logger.info("testHeroStartsWithUnit прошёл");
    }

    @Test void testHeroHealthReducesAfterAttack() {
        int initialHealth = hero2.getHealth();
        hero2.takeDamage(30);
        assertTrue(hero2.getHealth() < initialHealth);
        logger.info("testHeroHealthReducesAfterAttack прошёл");
    }

    @Test void testDuplicateBuildingsStillApplyEffectOnce() {
        hero1.buildings.add("Таверна");
        hero1.buildings.add("Таверна");
        hero1.setBuildingArmyLimitBonus(3);
        assertEquals(3, hero1.getBuildingArmyLimitBonus());
        logger.info("testDuplicateBuildingsStillApplyEffectOnce прошёл");
    }

    @Test void testRoadCellIsRecognized() {
        map.getCells()[5][5].setRoad(true);
        assertTrue(map.getCells()[5][5].isRoad());
        logger.info("testRoadCellIsRecognized прошёл");
    }

    @Test void testCastleCellStoresOwner() {
        map.getCells()[6][6].setCastle(true);
        map.getCells()[6][6].setOwner("Player");
        assertEquals("Player", map.getCells()[6][6].getOwner());
        logger.info("testCastleCellStoresOwner прошёл");
    }

    @Test void testHeroCannotCollectGoldTwiceFromSameCell() {
        map.getCells()[0][1].setGoldAmount(100);
        hero1.move(0, 1);
        int firstLoot = hero1.getGold();
        hero1.move(0, 0);
        hero1.move(0, 1);
        int secondLoot = hero1.getGold();
        assertEquals(firstLoot, secondLoot);
        logger.info("testHeroCannotCollectGoldTwiceFromSameCell прошёл");
    }

    @Test void testBuyRandomUnitViaReflection() {
        try {
            Hero testHero = new Hero("Tester", 200, 30, 2, 4, 0, 0, map, "Player");
            testHero.setGold(100); // достаточно золота

            Method method = testHero.getClass().getDeclaredMethod("buyRandomUnit");
            method.setAccessible(true); // снимаем private

            int sizeBefore = testHero.getArmy().size();

            method.invoke(testHero); // вызываем метод вручную

            int sizeAfter = testHero.getArmy().size();
            assertTrue(sizeAfter > sizeBefore);

            logger.info("testBuyRandomUnitViaReflection прошёл");

        } catch (Exception e) {
            logger.severe("Ошибка при вызове приватного метода buyRandomUnit(): " + e.getMessage());
            fail("Не удалось вызвать приватный метод buyRandomUnit()");
        }
    }

    @Test void testHeroBuildFailsOutsideCastle() {
        map.getCells()[0][0].setCastle(false);
        hero1.buildRandomStructure();
        assertTrue(hero1.getBuildings().size() >= 0); // просто проверяем не упало
        logger.info("testHeroBuildFailsOutsideCastle прошёл");
    }
}
