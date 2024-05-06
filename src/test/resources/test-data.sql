INSERT INTO tracktimestats (class_name, created_at, execution_time, group_name,
                            method_name, package_name, parameters, return_type,
                            method_status)
VALUES ('JavaService', '2024-05-10 13:21', 85, 'async', 'addClass', 'com.computers.lang.java.service', 'Class', 'void', 'COMPLETED'),
       ('JavaService', '2024-05-10 14:47', 120, 'async', 'addFunction', 'com.computers.lang.java.service', 'String', 'void', 'COMPLETED'),
       ('JavaScriptService', '2024-05-10 15:14', 55, 'async', 'addFunction', 'com.computers.lang.javascript.service', 'String', 'void', 'COMPLETED'),
       ('JavaScriptService', '2024-05-11 12:41', 23, 'async', 'addClass', 'com.computers.lang.javascript.service', 'Class', 'void', 'COMPLETED'),
       ('JavaScriptService', '2024-05-12 12:55', 48, 'async', 'addMethod', 'com.computers.lang.javascript.service', 'Method', 'void', 'COMPLETED'),
       ('JavaScriptService', '2024-05-12 13:12', 79, 'async', 'getMethod', 'com.computers.lang.javascript.repo', '', 'Method', 'EXCEPTION'),
       ('PythonRepository', '2024-05-12 15:42', 82, 'sync', 'addSuperClass', 'com.computers.lang.python.repo', 'Class', 'void', 'COMPLETED'),
       ('PythonRepository', '2024-05-14 14:01', 105, 'sync', 'addFunction', 'com.computers.lang.python.repo', 'String', 'void', 'COMPLETED'),
       ('PythonRepository', '2024-05-15 10:25', 123, 'async', 'removeFunction', 'com.computers.lang.python.repo', 'String', 'boolean', 'EXCEPTION');
