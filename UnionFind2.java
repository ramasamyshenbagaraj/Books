import java.util.*;

class Account {
    String source;
    String target;
    String securityId;
    String currency;
    int quantity;

    public Account(String source, String target, String securityId, String currency, int quantity) {
        this.source = source;
        this.target = target;
        this.securityId = securityId;
        this.currency = currency;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Account{source='" + source + "', target='" + target + "', securityId='" + securityId + "', currency='" + currency + "', quantity=" + quantity + '}';
    }
}

public class AccountMergerUnionFind {

    public static List<Account> mergeAccounts(List<Account> accounts) {
        // Map to store unique keys for each account
        Map<String, String> parentMap = new HashMap<>();
        Map<String, Account> accountMap = new HashMap<>();
        Map<String, String> targetMap = new HashMap<>();

        // Initialize Union-Find structure and target map
        for (Account account : accounts) {
            String sourceKey = generateKey(account.source, account.securityId, account.currency);
            String targetKey = generateKey(account.target, account.securityId, account.currency);

            accountMap.putIfAbsent(sourceKey, account);
            accountMap.putIfAbsent(targetKey, account);
            targetMap.put(sourceKey, targetKey);

            union(parentMap, sourceKey, targetKey);
        }

        // Group accounts by their representative parent
        Map<String, Set<String>> groupedComponents = new HashMap<>();
        for (String key : accountMap.keySet()) {
            String parent = find(parentMap, key);
            groupedComponents.computeIfAbsent(parent, k -> new HashSet<>()).add(key);
        }

        // Merge accounts within each group
        List<Account> mergedAccounts = new ArrayList<>();
        for (Set<String> component : groupedComponents.values()) {
            String source = null;
            String target = null;
            Account baseAccount = null;

            // Identify source and target for the component
            for (String key : component) {
                if (source == null || !targetMap.containsValue(key)) {
                    source = key; // Node without incoming edges
                }
                if (target == null || !targetMap.containsKey(key)) {
                    target = key; // Node without outgoing edges
                }
                baseAccount = accountMap.get(key); // Use any account in the component for metadata
            }

            if (source != null && target != null && baseAccount != null) {
                mergedAccounts.add(new Account(
                        getAccountId(source),
                        getAccountId(target),
                        baseAccount.securityId,
                        baseAccount.currency,
                        baseAccount.quantity
                ));
            }
        }

        return mergedAccounts;
    }

    private static String find(Map<String, String> parentMap, String account) {
        if (!parentMap.containsKey(account)) {
            return account; // If no parent, return self
        }
        String parent = find(parentMap, parentMap.get(account));
        parentMap.put(account, parent); // Path compression
        return parent;
    }

    private static void union(Map<String, String> parentMap, String account1, String account2) {
        String root1 = find(parentMap, account1);
        String root2 = find(parentMap, account2);

        if (!root1.equals(root2)) {
            parentMap.put(root2, root1); // Union operation
        }
    }

    private static String generateKey(String account, String securityId, String currency) {
        return account + "|" + securityId + "|" + currency;
    }

    private static String getAccountId(String key) {
        return key.split("\\|")[0]; // Extract the account ID from the key
    }

    public static void main(String[] args) {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("A1", "A2", "S1", "USD", 100));
        accounts.add(new Account("A2", "A3", "S1", "USD", 100));
        accounts.add(new Account("A4", "A5", "S2", "EUR", 200));
        accounts.add(new Account("A5", "A6", "S2", "EUR", 200));

        System.out.println("Original Accounts:");
        for (Account account : accounts) {
            System.out.println(account);
        }

        List<Account> mergedAccounts = mergeAccounts(accounts);

        System.out.println("\nMerged Accounts:");
        for (Account account : mergedAccounts) {
            System.out.println(account);
        }
    }
}
