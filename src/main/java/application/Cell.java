package application;

/**
 * Класс Cell представляет ячейку игрового поля, которая может содержать
 * информацию о владельце, наличии воды, риса, защите, количестве необходимых
 * домов и ресурсов, биоме и наличии леса и деревьев.
 */
public class Cell {
    public String ownerFlag = "Free";
    public boolean waterCell, riceCell, protection = false;
    public int needOfHouses, numberOfRice = 0, numberOfWater = 0, numberOfHouses = 0;

    public String biome = null;
    public boolean forest, tree;

    /**
     * Возвращает краткое описание ячейки в виде строки.
     *
     * @return Краткое описание в формате "Владелец_Необходимые дома_Количество
     *         воды_Количество риса_ Количество домов_Наличие воды_Наличие
     *         риса_Наличие защиты_Биом_Наличие леса_Наличие деревьев".
     */
    public String shortDescription() {
        return (ownerFlag.equals("Free") ? "Free" : (ownerFlag.equals("Player") ? "Player" : "PlayerAi")) + "_"
                + needOfHouses + "_" + numberOfWater + "_" + numberOfRice + "_" + numberOfHouses + "_"
                + (waterCell ? "T" : "F") + "_" + (riceCell ? "T" : "F") + "_" + (protection ? "T" : "F") + "_"
                + (biome.equals("Grass") ? "Grass" : (biome.equals("Water") ? "Water" : "Rice")) + "_"
                + (forest ? "T" : "F") + "_" + (tree ? "T" : "F");
    }

    /**
     * Возвращает краткое описание ячейки в виде строки для карты текста.
     *
     * @return Краткое описание в формате "Владелец_Необходимые дома_Количество
     *         воды_Количество риса_Количество домов_Наличие воды_Наличие
     *         риса_Наличие защиты".
     */
    public String descriptionToTextMap() {
        return (ownerFlag.equals("Free") ? "Free" : (ownerFlag.equals("Player") ? "Player" : "PlayerAi")) + "_"
                + needOfHouses + "_" + numberOfWater + "_" + numberOfRice + "_" + numberOfHouses + "_"
                + (waterCell ? "T" : "F") + "_" + (riceCell ? "T" : "F") + "_" + (protection ? "T" : "F");
    }
}
