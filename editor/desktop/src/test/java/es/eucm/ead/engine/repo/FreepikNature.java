package es.eucm.ead.engine.repo;

import es.eucm.ead.editor.demobuilder.RepoLibraryBuilder;
import es.eucm.ead.schema.editor.components.repo.RepoLicense;

/**
 * Created by Javier Torrente on 25/09/14.
 */
public class FreepikNature extends RepoLibraryBuilder{
    public FreepikNature(){
        super("freepik-nature-01");
    }

    private String[] files = {"butterfly_01.png",
            "butterfly_02.png",
            "butterfly_03.png",
            "butterfly_04.png",
            "butterfly_05.png",
            "butterfly_06.png",
            "fruit_01.png",
            "fruit_02.png",
            "fruit_03.png",
            "fruit_04.png",
            "fruit_05.png",
            "fruit_06.png",
            "fruit_07.png",
            "fruit_08.png",
            "fruit_09.png",
            "fruit_10.png",
            "fruit_11.png",
            "fruit_12.png",
            "fruit_13.png",
            "fruit_14.png",
            "fruit_15.png",
            "fruit_16.png",
            "landscape.png",
            "landscape02.png",
            "mushroom.png",
            "tree_0000_Objeto-inteligente-vectorial.png",
            "tree_0001_Objeto-inteligente-vectorial.png",
            "tree_0002_Objeto-inteligente-vectorial.png",
            "tree_0003_Objeto-inteligente-vectorial.png",
            "tree_0004_Objeto-inteligente-vectorial.png",
            "tree_0005_Objeto-inteligente-vectorial.png",
            "tree_0006_Objeto-inteligente-vectorial.png",
            "tree_0007_Objeto-inteligente-vectorial.png",
            "tree_0008_Objeto-inteligente-vectorial.png",
            "tree_0009_Objeto-inteligente-vectorial.png",
            "tree_0010_Objeto-inteligente-vectorial.png",
            "tree_0011_Objeto-inteligente-vectorial.png",
            "tree_0012_Objeto-inteligente-vectorial.png",
            "tree_0013_Objeto-inteligente-vectorial.png",
            "tree_0014_Objeto-inteligente-vectorial.png",
            "tree_0015_Objeto-inteligente-vectorial.png",
            "tree_0016_Objeto-inteligente-vectorial.png",
            "tree_0017_Objeto-inteligente-vectorial.png",
            "tree_0018_Objeto-inteligente-vectorial.png",
            "tree_0019_Objeto-inteligente-vectorial.png",
            "tree_0020_Objeto-inteligente-vectorial.png",
            "tree_0021_Objeto-inteligente-vectorial.png",
            "tree_0022_Objeto-inteligente-vectorial.png",
            "tree_0023_Objeto-inteligente-vectorial.png",
            "tree_0024_Objeto-inteligente-vectorial.png",
            "tree_0025_Objeto-inteligente-vectorial.png",
            "tree_0026_Objeto-inteligente-vectorial.png",
            "tree_0027_Objeto-inteligente-vectorial.png",
            "tree_0028_Objeto-inteligente-vectorial.png",
            "tree_0029_Objeto-inteligente-vectorial.png",
            "tree_0030_Objeto-inteligente-vectorial.png",
            "tree_0031_Objeto-inteligente-vectorial.png",
            "tree_0032_Objeto-inteligente-vectorial.png",
            "tree_0033_Objeto-inteligente-vectorial.png",
            "tree_0034_Objeto-inteligente-vectorial.png",
            "tree_0035_Objeto-inteligente-vectorial.png",
            "tree_0036_Objeto-inteligente-vectorial.png",
            "tree_0037_Objeto-inteligente-vectorial.png",
            "tree_0038_Objeto-inteligente-vectorial.png",
            "tree_0039_Objeto-inteligente-vectorial.png",
            "tree_0040_Objeto-inteligente-vectorial.png",
            "tree_0041_Objeto-inteligente-vectorial.png",
            "tree_0042_Objeto-inteligente-vectorial.png",
            "tree_0043_Objeto-inteligente-vectorial.png",
            "tree_0044_Objeto-inteligente-vectorial.png",
            "tree_0045_Objeto-inteligente-vectorial.png",
            "tree_0046_Objeto-inteligente-vectorial.png",
            "tree_0047_Objeto-inteligente-vectorial.png",
            "tree_0048_Objeto-inteligente-vectorial.png",
            "tree_0049_Objeto-inteligente-vectorial.png",
            "tree_0050_Objeto-inteligente-vectorial.png",
            "tree_0051_Objeto-inteligente-vectorial.png",
            "tree_0052_Objeto-inteligente-vectorial.png",
            "tree_0053_Objeto-inteligente-vectorial.png",
            "tree_0054_Objeto-inteligente-vectorial.png",
            "tree_0055_Objeto-inteligente-vectorial.png",
            "tree_0056_Objeto-inteligente-vectorial.png",
            "tree_0057_Objeto-inteligente-vectorial.png",
            "tree_0058_Objeto-inteligente-vectorial.png",
            "tree_0059_Objeto-inteligente-vectorial.png",
            "tree_0060_Objeto-inteligente-vectorial.png",
            "tree_0061_Objeto-inteligente-vectorial.png",
            "tree_0062_Objeto-inteligente-vectorial.png",
            "tree_0063_Objeto-inteligente-vectorial.png"};

    @Override
    protected void doBuild() {
        setCommonProperty(RESOURCES, "");
        setCommonProperty(THUMBNAILS, "");
        setCommonProperty(TAGS, "Freepik,Nature;Naturaleza,Cartoon,Forest;Bosque");
        setCommonProperty(AUTHOR_NAME, "FreePik");
        setCommonProperty(AUTHOR_URL, "http://www.freepik.com/");
        setCommonProperty(LICENSE, RepoLicense.LINK_AUTHOR.toString());

        int nTrees=1,nButterflies=1,nFruit=1, nLand=1;
        for (String file: files){
            if (file.startsWith("tree")){
                setCommonProperty(MAX_WIDTH, "300");
                setCommonProperty(MAX_HEIGHT, "300");
                repoEntity("Tree "+nTrees, "Árbol "+nTrees, "", "", file, file).adjustEntity(getLastEntity());
                nTrees++;
            } else if (file.startsWith("butterfly")){
                setCommonProperty(MAX_WIDTH, "100");
                setCommonProperty(MAX_HEIGHT, "100");
                repoEntity("Butterfly "+nButterflies, "Mariposa "+nButterflies, "", "", file, file).adjustEntity(getLastEntity());
                nButterflies++;
            } else if (file.startsWith("fruit")){
                setCommonProperty(MAX_WIDTH, "150");
                setCommonProperty(MAX_HEIGHT, "150");
                repoEntity("Fruit "+nFruit, "Fruta "+nFruit, "", "", file, file).adjustEntity(getLastEntity());
                nFruit++;
            }  else if (file.startsWith("mushroom")){
                setCommonProperty(MAX_WIDTH, "200");
                setCommonProperty(MAX_HEIGHT, "200");
                repoEntity("Purple mushroom", "Champiñón morado", "", "", file, file).adjustEntity(getLastEntity());
            } else if (file.startsWith("landscape")){
                setCommonProperty(MAX_WIDTH, "1024");
                setCommonProperty(MAX_HEIGHT, "1024");
                repoEntity("Natural Landscape "+nLand, "Paisaje natural "+nLand, "", "", file, file).tag
                        ("background", "paisaje").adjustEntity(getLastEntity());
                nLand++;
            }
        }

        repoLib("Freepik natural cartoon compilation 1", "Freepik - Compilación de recursos naturales 1","","",null);

    }

    public static void main (String[]args){
        FreepikNature lib = new FreepikNature();
        lib.export("D:\\Documents\\TRABAJO\\E-ADVENTURE\\Mockup\\Repo\\");
    }
}
