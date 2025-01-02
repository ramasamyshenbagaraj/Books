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
        return "Account{source='" + source + "', target='" + target + "', securityId='" + securityId + 
               "', currency='" + currency + "', quantity=" + quantity + '}';
    }
}

public class AccountMergerWithSecurity {

    public static List<Account> mergeAccounts(List<Account> accounts) {
        // Build adjacency list (graph representation) based on uniqueness criteria
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Account> accountMap = new HashMap<>();

        for (Account account : accounts) {
            String sourceKey = generateKey(account.source, account.securityId, account.currency);
            String targetKey = generateKey(account.target, account.securityId, account.currency);

            graph.computeIfAbsent(sourceKey, k -> new ArrayList<>()).add(targetKey);
            graph.computeIfAbsent(targetKey, k -> new ArrayList<>()).add(sourceKey);

            accountMap.putIfAbsent(sourceKey, account);
            accountMap.putIfAbsent(targetKey, account);
        }

        // Track visited nodes
        Set<String> visited = new HashSet<>();
        List<Account> mergedAccounts = new ArrayList<>();

        // Traverse graph to find connected components
        for (String key : graph.keySet()) {
            if (!visited.contains(key)) {
                List<String> component = new ArrayList<>();
                dfs(key, graph, visited, component);

                // Use the first and last nodes in the component
                String firstKey = component.get(0);
                String lastKey = component.get(component.size() - 1);

                Account firstAccount = accountMap.get(firstKey);

                // Check if the lastKey exists in the accountMap, fallback to firstKey's target if missing
                String target = accountMap.containsKey(lastKey) ? accountMap.get(lastKey).target : firstAccount.target;

                mergedAccounts.add(new Account(
                        firstAccount.source,
                        target,
                        firstAccount.securityId,
                        firstAccount.currency,
                        firstAccount.quantity
                ));
            }
        }

        return mergedAccounts;
    }

    private static void dfs(String node, Map<String, List<String>> graph, Set<String> visited, List<String> component) {
        visited.add(node);
        component.add(node);
        for (String neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, graph, visited, component);
            }
        }
    }

    private static String generateKey(String account, String securityId, String currency) {
        return account + "|" + securityId + "|" + currency;
    }

    public static void main(String[] args) {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("A1", "A2", "SEC123", "USD", 100));
        accounts.add(new Account("A2", "A3", "SEC123", "USD", 100));
        accounts.add(new Account("A4", "A5", "SEC456", "EUR", 200));

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
