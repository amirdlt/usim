package com.usim.engine.engine.entity.loader;

import com.usim.engine.engine.graph.Mesh;
import com.usim.ulib.utils.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModelLoader {
    public static @NotNull Mesh loadTriangularFacesMesh(String fileName) throws IOException {
        var lines = Utils.getFileAsString(fileName).split("\n");
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        record VI(int pos, int tex, int n) {}
        record Face(@NotNull String _v1, @NotNull String _v2, @NotNull String _v3) {
            @Contract("_ -> new")
            private static @NotNull VI process(@NotNull String v) {
                var tokens = v.split("/+");
                return new VI(Integer.parseInt(tokens[0]) - 1,
                        tokens.length > 1 && tokens[1].length() > 0 ? Integer.parseInt(tokens[1]) - 1 : -1,
                        tokens.length > 2 ? Integer.parseInt(tokens[2]) - 1 : -1);
            }
            @Contract(" -> new")
            @NotNull VI v1() {return process(_v1);}
            @Contract(" -> new")
            @NotNull VI v2() {return process(_v2);}
            @Contract(" -> new")
            @NotNull VI v3() {return process(_v3);}
        }
        List<Face> faces = new ArrayList<>();
        for (var tokens : Arrays.stream(lines).map(s -> s.split("\\s+")).toList()) {
            switch (tokens[0]) {
                case "v" -> vertices.add(
                        new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                case "vt" -> textures.add(new Vector2f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                case "vn" -> normals.add(
                        new Vector3f(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                case "f" -> faces.add(new Face(tokens[1], tokens[2],tokens[3]));
            }
        }
        var _vertices = new float[3 * vertices.size()];
        var _textures = new float[2 * textures.size()];
        var _normals = new float[3 * normals.size()];
        int index = 0;
        while (index < _vertices.length)
            _vertices[index++] = index % 3 == 0 ?
                    vertices.get(index / 3).x :
                    index % 3 == 1 ? vertices.get(index / 3).y : vertices.get(index / 3).z;
        index = 0;
        while (index < _textures.length)
            _textures[index++] = index % 2 == 0 ? textures.get(index / 2).x : textures.get(index / 2).y;
        index = 0;
        while (index < _normals.length)
            _normals[index++] =
                    index % 3 == 0 ? normals.get(index / 3).x : index % 3 == 1 ? normals.get(index / 3).y : normals.get(index / 3).z;
        var _indices = new float[3 * faces.size()];

//        return Mesh(_vertices, _textures, _normals, _indices);
        return null;
    }
}
