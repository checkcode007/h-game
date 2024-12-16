package com.z.common.util;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import org.apache.logging.log4j.util.Strings;
public class PbUtils {
    public static String pbToJson(MessageLiteOrBuilder message) {
        try {
            // 使用 JsonFormat 打印默认值并生成 JSON 字符串
            String jsonString = JsonFormat.printer()
                    .includingDefaultValueFields()
                    .print((MessageOrBuilder) message);

            // 对目标字段进行处理
            jsonString = removeLineBreaksInArrays(jsonString, "goals");
            jsonString = removeLineBreaksInArrays(jsonString, "mjs");

            return jsonString;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return Strings.EMPTY;
    }

    /**
     * 该方法用于去除目标数组中的换行符
     *
     * @param jsonString 原始的 JSON 字符串
     * @param arrayName  需要处理的数组名（goals 或 mjs）
     * @return 处理后的 JSON 字符串
     */
    private static String removeLineBreaksInArrays(String jsonString, String arrayName) {
        String regex = String.format("\"%s\":\\s*\\[", arrayName);
        jsonString = jsonString.replaceAll(regex, "\"" + arrayName + "\":[");

        jsonString = jsonString.replaceAll("\\s+", " ");

        return jsonString;
    }
    public static String pbToJson(AbstractMessageLite msg){
        try {
            MessageOrBuilder message =(MessageOrBuilder) msg;
            String jsonString =JsonFormat.printer().includingDefaultValueFields().print(message);
                    // 对目标字段进行处理
            jsonString = removeLineBreaksInArrays(jsonString, "goals");
            jsonString = removeLineBreaksInArrays(jsonString, "mjs");
            return jsonString;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        return Strings.EMPTY;
    }

    public static void main(String[] args) {
        String s ="{\n" +
                "  \"mj\": {\n" +
                "    \"roundId\": \"2\",\n" +
                "    \"leaveGold\": \"242000\",\n" +
                "    \"gold\": \"1250\",\n" +
                "    \"mjOnes\": [{\n" +
                "      \"goals\": [{\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 3,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 3,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 4,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 3,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"B_5\",\n" +
                "        \"x\": 4,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 1\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 1\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 1\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 2\n" +
                "      }, {\n" +
                "        \"type\": \"T_2\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 2\n" +
                "      }],\n" +
                "      \"mjs\": [],\n" +
                "      \"free\": 0,\n" +
                "      \"gold\": \"650\"\n" +
                "    }, {\n" +
                "      \"goals\": [{\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 4\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 4\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 3,\n" +
                "        \"y\": 4\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 4\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 3,\n" +
                "        \"y\": 4\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 4,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 0,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 3\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 0\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 2,\n" +
                "        \"y\": 4\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 3,\n" +
                "        \"y\": 4\n" +
                "      }, {\n" +
                "        \"type\": \"T_5\",\n" +
                "        \"x\": 4,\n" +
                "        \"y\": 3\n" +
                "      }],\n" +
                "      \"mjs\": [],\n" +
                "      \"free\": 0,\n" +
                "      \"gold\": \"600\"\n" +
                "    }],\n" +
                "    \"mjs\": [{\n" +
                "      \"type\": \"B_2\",\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 3\n" +
                "    }, {\n" +
                "      \"type\": \"T_2\",\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 1\n" +
                "    }, {\n" +
                "      \"type\": \"T_2\",\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 2\n" +
                "    }, {\n" +
                "      \"type\": \"T_5\",\n" +
                "      \"x\": 0,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 1,\n" +
                "      \"y\": 1\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 1,\n" +
                "      \"y\": 4\n" +
                "    }, {\n" +
                "      \"type\": \"B_5\",\n" +
                "      \"x\": 1,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"B_5\",\n" +
                "      \"x\": 1,\n" +
                "      \"y\": 1\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 1,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 2,\n" +
                "      \"y\": 1\n" +
                "    }, {\n" +
                "      \"type\": \"B_5\",\n" +
                "      \"x\": 2,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"B_5\",\n" +
                "      \"x\": 2,\n" +
                "      \"y\": 1\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 2,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 2,\n" +
                "      \"y\": 1\n" +
                "    }, {\n" +
                "      \"type\": \"B_2\",\n" +
                "      \"x\": 3,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 3,\n" +
                "      \"y\": 1\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 3,\n" +
                "      \"y\": 2\n" +
                "    }, {\n" +
                "      \"type\": \"B_2\",\n" +
                "      \"x\": 3,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"T_5\",\n" +
                "      \"x\": 3,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"B_2\",\n" +
                "      \"x\": 4,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"B_3\",\n" +
                "      \"x\": 4,\n" +
                "      \"y\": 1\n" +
                "    }, {\n" +
                "      \"type\": \"B_2\",\n" +
                "      \"x\": 4,\n" +
                "      \"y\": 0\n" +
                "    }, {\n" +
                "      \"type\": \"T_5\",\n" +
                "      \"x\": 4,\n" +
                "      \"y\": 0\n" +
                "    }]\n" +
                "  }\n" +
                "}";

//        s = s.replaceAll("\\{(.*?)\\}", "{$1}"); // 去掉换行
//        s = s.replaceAll("\\},\\s*\\{", "}, {"); // 修正数组中元素之间的换行
//        s = s.replaceAll(",\\s*\\}", "}"); // 移除最后一个对象的逗号

        // 去除所有换行符和多余的空格，确保输出单行
//        s = s.replaceAll("\\s+", " "); // 替换掉所有的换行和多余的空格
//        s = s.trim();  // 去除两端的空格

        s = s.replaceAll("\"goals\":\\s*\\[\\s*\\{", "\"goals\":[{\"")
                .replaceAll("\\},\\s*\\{", "},{")
                .replaceAll("\\}\\s*\\]", "}]");

        s = s.replaceAll("\"mjs\":\\s*\\[\\s*\\{", "\"mjs\":[{\"")
                .replaceAll("\\},\\s*\\{", "},{")
                .replaceAll("\\}\\s*\\]", "}]");

        // 去掉多余的空格
        s = s.trim();
        // 对目标字段进行处理
        s = removeLineBreaksInArrays(s, "goals");
        s = removeLineBreaksInArrays(s, "mjs");


        System.err.println(s);
    }

}
