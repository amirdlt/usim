package ahd.usim.engine.entity.loader;

import ahd.usim.engine.entity.mesh.AbstractMesh;
import ahd.usim.engine.entity.mesh.ImmutableMesh;
import ahd.usim.ulib.utils.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {
//    public static @NotNull Mesh loadTriangularFacesMesh(String fileName) throws IOException {
//        var lines = Utils.getFileAsString(fileName).split("\n");
//        List<Vector3f> vertices = new ArrayList<>();
//        List<Vector2f> textures = new ArrayList<>();
//        List<Vector3f> normals = new ArrayList<>();
//        record VI(int pos, int tex, int n) {}
//        record Face(@NotNull String _v1, @NotNull String _v2, @NotNull String _v3) {
//            @Contract("_ -> new")
//            private static @NotNull VI process(@NotNull String v) {
//                var tokens = v.split("/+");
//                return new VI(Integer.parseInt(tokens[0]) - 1,
//                        tokens.length > 1 && tokens[1].length() > 0 ? Integer.parseInt(tokens[1]) - 1 : -1,
//                        tokens.length > 2 ? Integer.parseInt(tokens[2]) - 1 : -1);
//            }
//            @Contract(" -> new")
//            @NotNull VI v1() {return process(_v1);}
//            @Contract(" -> new")
//            @NotNull VI v2() {return process(_v2);}
//            @Contract(" -> new")
//            @NotNull VI v3() {return process(_v3);}
//        }
//        List<Face> faces = new ArrayList<>();
//        for (var tokens : Arrays.stream(lines).map(s -> s.split("\\s+")).toList()) {
//            switch (tokens[0]) {
//                case "v" -> vertices.add(
//                        new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
//                case "vt" -> textures.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
//                case "vn" -> normals.add(
//                        new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
//                case "f" -> faces.add(new Face(tokens[1], tokens[2],tokens[3]));
//            }
//        }
//        var _vertices = new float[3 * vertices.size()];
//        var _textures = new float[2 * textures.size()];
//        var _normals = new float[3 * normals.size()];
//        int index = 0;
//        while (index < _vertices.length)
//            _vertices[index++] = index % 3 == 0 ?
//                    vertices.get(index / 3).x :
//                    index % 3 == 1 ? vertices.get(index / 3).y : vertices.get(index / 3).z;
//        index = 0;
//        while (index < _textures.length)
//            _textures[index++] = index % 2 == 0 ? textures.get(index / 2).x : textures.get(index / 2).y;
//        index = 0;
//        while (index < _normals.length)
//            _normals[index++] =
//                    index % 3 == 0 ? normals.get(index / 3).x : index % 3 == 1 ? normals.get(index / 3).y : normals.get(index / 3).z;
//        var _indices = new float[3 * faces.size()];
//
////        return Mesh(_vertices, _textures, _normals, _indices);
//        return null;
//    }

    public static @NotNull AbstractMesh loadMesh(String fileName) throws IOException {
        var lines = Utils.getFileAsString(fileName).split("\n");

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    // Geometric vertex
                    Vector3f vec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    // Texture coordinate
                    Vector2f vec2f = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    // Vertex normal
                    Vector3f vec3fNorm = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(vec3fNorm);
                    break;
                case "f":
                    Face face = new Face(tokens[1], tokens[2], tokens[3]);
                    faces.add(face);
                    break;
                default:
                    // Ignore other lines
                    break;
            }
        }
        return reorderLists(vertices, textures, normals, faces);
    }

    @Contract("_, _, _, _ -> new")
    private static @NotNull AbstractMesh reorderLists(@NotNull List<Vector3f> posList, List<Vector2f> textCoordList,
            List<Vector3f> normList, List<Face> facesList) {

        List<Integer> indices = new ArrayList<>();
        // Create position array in the order it has been declared
        float[] posArr = new float[posList.size() * 3];
        int i = 0;
        for (Vector3f pos : posList) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] textCoordArr = new float[posList.size() * 2];
        float[] normArr = new float[posList.size() * 3];

        for (Face face : facesList) {
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList,
                        indices, textCoordArr, normArr);
            }
        }
        return new ImmutableMesh(posArr, null, textCoordArr, normArr, indices.stream().mapToInt((Integer v) -> v).toArray(), GL11.GL_TRIANGLES);
    }

    private static void processFaceVertex(@NotNull IdxGroup indices, List<Vector2f> textCoordList,
            List<Vector3f> normList, @NotNull List<Integer> indicesList,
            float[] texCoordArr, float[] normArr) {

        // Set index for vertex coordinates
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);

        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            // Reorder vectornormals
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }

    protected static class Face {

        /**
         * List of idxGroup groups for a face triangle (3 vertices per face).
         */
        private IdxGroup[] idxGroups = new IdxGroup[3];

        public Face(String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];
            // Parse the lines
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private @NotNull IdxGroup parseLine(@NotNull String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }

    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos;

        public int idxTextCoord;

        public int idxVecNormal;

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }
}
