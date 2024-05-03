.PHONY: create_table drop_table
create_table:
	psql -U postgres -d postgres -a -f migrations/V1__Create_table.sql

drop_table:
	psql -U postgres -d postgres -a -f migrations/V1__Drop_table.sql