ALTER TABLE categories
    ALTER COLUMN id SET DEFAULT nextval('categories_seq');

INSERT INTO categories (display_name, name, description, expense_type)
VALUES
    -- Income Categories
    ('Salary', 'income_salary', 'Monthly salary and wages', 'INCOME'),
    ('Freelance Work', 'income_freelance', 'Earnings from freelance jobs', 'INCOME'),
    ('Investments', 'income_investments', 'Dividends, stocks, and other investment income', 'INCOME'),
    ('Business Income', 'income_business', 'Revenue from business activities', 'INCOME'),
    ('Rental Income', 'income_rental', 'Income from property rentals', 'INCOME'),
    ('Bonuses', 'income_bonuses', 'Performance-based bonuses and commissions', 'INCOME'),
    ('Gifts Received', 'income_gifts', 'Monetary gifts received', 'INCOME'),
    ('Other Income', 'income_other', 'Miscellaneous income sources', 'INCOME'),

    -- Expense Categories
    ('Rent/Mortgage', 'housing_rent', 'Monthly rent or mortgage payments', 'EXPENSE'),
    ('Utilities', 'housing_utilities', 'Electricity, water, gas, and other utilities', 'EXPENSE'),
    ('Internet & Cable', 'housing_internet', 'Internet and cable TV expenses', 'EXPENSE'),
    ('Fuel', 'transport_fuel', 'Fuel for personal vehicles', 'EXPENSE'),
    ('Public Transport', 'transport_public', 'Bus, train, taxi, and rideshare expenses', 'EXPENSE'),
    ('Vehicle Maintenance', 'transport_maintenance', 'Car servicing, repairs, and insurance', 'EXPENSE'),
    ('Groceries', 'food_groceries', 'Supermarket and grocery store expenses', 'EXPENSE'),
    ('Dining Out', 'food_dining', 'Restaurants, fast food, and takeout', 'EXPENSE'),
    ('Coffee & Snacks', 'food_coffee', 'Cafes, snacks, and beverages', 'EXPENSE'),
    ('Health Insurance', 'health_insurance', 'Monthly health insurance premiums', 'EXPENSE'),
    ('Medical Bills', 'health_medical', 'Doctor visits, prescriptions, and medical treatments', 'EXPENSE'),
    ('Fitness & Gym', 'health_fitness', 'Gym memberships and fitness-related costs', 'EXPENSE'),
    ('Movies & Streaming', 'entertainment_movies', 'Cinema tickets and streaming subscriptions', 'EXPENSE'),
    ('Hobbies', 'entertainment_hobbies', 'Books, music, arts, and other hobbies', 'EXPENSE'),
    ('Events & Concerts', 'entertainment_events', 'Concerts, sports, and entertainment events', 'EXPENSE'),
    ('Loan Repayments', 'debt_loans', 'Monthly loan payments', 'EXPENSE'),
    ('Credit Card Bills', 'debt_credit_cards', 'Credit card payments and interest', 'EXPENSE'),
    ('Emergency Fund', 'savings_emergency', 'Savings for unexpected expenses', 'EXPENSE'),
    ('Retirement Savings', 'savings_retirement', 'Funds for future retirement plans', 'EXPENSE'),
    ('Tuition', 'education_tuition', 'School and university tuition fees', 'EXPENSE'),
    ('Books & Courses', 'education_books', 'Educational books, courses, and training materials', 'EXPENSE'),
    ('Clothing', 'personal_clothing', 'Shopping for clothes and accessories', 'EXPENSE'),
    ('Haircuts & Beauty', 'personal_beauty', 'Salon, haircuts, and beauty treatments', 'EXPENSE'),
    ('Charity', 'gifts_charity', 'Donations to charitable organizations', 'EXPENSE'),
    ('Gifts', 'gifts_general', 'Birthday and holiday gifts for family and friends', 'EXPENSE'),
    ('Other Expenses', 'misc_other', 'Uncategorized expenses', 'EXPENSE');