-- 小区保安管理系统数据库初始化

CREATE TABLE IF NOT EXISTS records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    plate_number TEXT NOT NULL,
    phone TEXT NOT NULL,
    building TEXT NOT NULL,
    room TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_plate ON records(plate_number);

CREATE TABLE IF NOT EXISTS settings (
    id INTEGER PRIMARY KEY CHECK (id = 1),
    compound_name TEXT DEFAULT '阳光小区',
    admin_phone TEXT DEFAULT '',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT OR IGNORE INTO settings (id, compound_name, admin_phone) VALUES (1, '阳光小区', '');
