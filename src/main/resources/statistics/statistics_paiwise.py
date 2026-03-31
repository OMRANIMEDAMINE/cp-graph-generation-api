import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import math

# Set up plotting style
plt.style.use('seaborn-v0_8')
sns.set_palette("husl")


def load_and_preprocess_data(file_path):
    """
    Load data from Excel file and preprocess it for analysis
    """
    try:
        # Read the Excel file
        df = pd.read_excel(file_path, sheet_name='Feuille 1')

        # Clean column names (remove any extra spaces)
        df.columns = df.columns.str.strip()

        # Extract graph properties from the 'Graph' column
        df[['Graph_Type', 'Parameter']] = df['Graph'].str.extract(r'([K]\d+)\s*\((\d+)\)')
        df['Graph_Size'] = df['Graph_Type'].str.extract(r'K(\d+)').astype(int)
        df['Parameter'] = df['Parameter'].astype(int)

        # Check for duplicates
        duplicates = df[df.duplicated(subset=['Lex Count', 'Lex CPU', 'RevLex Count', 'RevLex CPU',
                                              'OptLex Count', 'OptLex CPU', 'OptRevLex Count', 'OptRevLex CPU'], keep=False)]
        if not duplicates.empty:
            print(f"Warning: {len(duplicates)} duplicate rows detected (possible placeholders for larger graphs).")

        print(f"Successfully loaded data with {len(df)} rows")
        print(f"Available columns: {list(df.columns)}")
        return df

    except Exception as e:
        print(f"Error loading file: {e}")
        return None


# Lex vs RevLex CPU Time Comparison
def plot_lex_revlex_cpu(df):
    """
    Plot CPU time comparison between Lex and RevLex
    """
    parameters = sorted(df['Parameter'].unique())
    num_params = len(parameters)
    cols = 2
    rows = math.ceil(num_params / cols)
    fig, axes = plt.subplots(rows, cols, figsize=(15, 6 * rows))
    axes = axes.flatten()
    #fig.suptitle('Lex vs RevLex: CPU Time Performance', fontsize=16, fontweight='bold')

    for i, param in enumerate(parameters):
        ax = axes[i]
        param_data = df[df['Parameter'] == param].sort_values('Graph_Size')
        if len(param_data) < 2:
            continue  # Skip if insufficient data points

        # Lex vs RevLex CPU comparison
        ax.plot(param_data['Graph_Size'], param_data['Lex CPU'],
                marker='o', label='Lex', linewidth=2.5, markersize=6, color='blue')
        ax.plot(param_data['Graph_Size'], param_data['RevLex CPU'],
                marker='s', label='RevLex', linewidth=2.5, markersize=6, color='red')

        ax.set_yscale('log', nonpositive='clip')
        ax.set_xlabel('Number of Nodes (n)')
        ax.set_ylabel('CPU Time (log scale)')
        # Place Kn(param) text inside the plot
        ax.text(0.25, 0.96, f'Kn({param})', transform=ax.transAxes, fontsize=12, fontweight='bold',
                verticalalignment='top', horizontalalignment='left',
                bbox=dict(boxstyle='round', facecolor='white', alpha=0.8))
        ax.legend(fontsize=11, loc='upper left')
        ax.grid(True, alpha=0.3)

        # Add ratio annotation in bottom-right corner
        if len(param_data) > 0:
            last_lex_cpu = param_data['Lex CPU'].iloc[-1]
            last_revlex_cpu = param_data['RevLex CPU'].iloc[-1]
            ratio = last_revlex_cpu / last_lex_cpu if last_lex_cpu > 0 else float('inf')
            ax.text(0.98, 0.02, f'RevLex/Lex Ratio: {ratio:.2f}x',
                    transform=ax.transAxes, fontsize=10, verticalalignment='bottom', horizontalalignment='right',
                    bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.8))

    # Remove empty subplots
    for j in range(num_params, len(axes)):
        fig.delaxes(axes[j])

    plt.tight_layout()
    plt.savefig('lex_revlex_cpu_comparison.png', dpi=300, bbox_inches='tight')
    plt.show()


# OptLex vs OptRevLex CPU Time Comparison
def plot_optlex_optrevlex_cpu(df):
    """
    Plot CPU time comparison between OptLex and OptRevLex
    """
    parameters = sorted(df['Parameter'].unique())
    num_params = len(parameters)
    cols = 2
    rows = math.ceil(num_params / cols)
    fig, axes = plt.subplots(rows, cols, figsize=(15, 6 * rows))
    axes = axes.flatten()
    #fig.suptitle('OptLex vs OptRevLex: CPU Time Performance', fontsize=16, fontweight='bold')

    for i, param in enumerate(parameters):
        ax = axes[i]
        param_data = df[df['Parameter'] == param].sort_values('Graph_Size')
        if len(param_data) < 2:
            continue  # Skip if insufficient data points

        # OptLex vs OptRevLex CPU comparison
        ax.plot(param_data['Graph_Size'], param_data['OptLex CPU'],
                marker='o', label='OptLex', linewidth=2.5, markersize=6, color='green')
        ax.plot(param_data['Graph_Size'], param_data['OptRevLex CPU'],
                marker='s', label='OptRevLex', linewidth=2.5, markersize=6, color='orange')

        ax.set_yscale('log', nonpositive='clip')
        ax.set_xlabel('Number of Nodes (n)')
        ax.set_ylabel('CPU Time (log scale)')
        # Place Kn(param) text inside the plot
        ax.text(0.25, 0.96, f'Kn({param})', transform=ax.transAxes, fontsize=12, fontweight='bold',
                verticalalignment='top', horizontalalignment='left',
                bbox=dict(boxstyle='round', facecolor='white', alpha=0.8))
        ax.legend(fontsize=11, loc='upper left')
        ax.grid(True, alpha=0.3)

        # Add ratio annotation in bottom-right corner
        if len(param_data) > 0:
            last_optlex_cpu = param_data['OptLex CPU'].iloc[-1]
            last_optrevlex_cpu = param_data['OptRevLex CPU'].iloc[-1]
            ratio = last_optrevlex_cpu / last_optlex_cpu if last_optlex_cpu > 0 else float('inf')
            ax.text(0.98, 0.02, f'OptRevLex/OptLex Ratio: {ratio:.2f}x',
                    transform=ax.transAxes, fontsize=10, verticalalignment='bottom', horizontalalignment='right',
                    bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.8))

    # Remove empty subplots
    for j in range(num_params, len(axes)):
        fig.delaxes(axes[j])

    plt.tight_layout()
    plt.savefig('optlex_optrevlex_cpu_comparison.png', dpi=300, bbox_inches='tight')
    plt.show()


# Lex vs RevLex Count Comparison
def plot_lex_revlex_count(df):
    """
    Plot count comparison between Lex and RevLex
    """
    parameters = sorted(df['Parameter'].unique())
    num_params = len(parameters)
    cols = 2
    rows = math.ceil(num_params / cols)
    fig, axes = plt.subplots(rows, cols, figsize=(15, 6 * rows))
    axes = axes.flatten()
    #fig.suptitle('Lex vs RevLex: Number of Generated Regular Graphs', fontsize=16, fontweight='bold')

    for i, param in enumerate(parameters):
        ax = axes[i]
        param_data = df[df['Parameter'] == param].sort_values('Graph_Size')
        if len(param_data) < 2:
            continue  # Skip if insufficient data points

        # Lex vs RevLex Count comparison
        ax.plot(param_data['Graph_Size'], param_data['Lex Count'],
                marker='o', label='Lex', linewidth=2.5, markersize=6, color='blue')
        ax.plot(param_data['Graph_Size'], param_data['RevLex Count'],
                marker='s', label='RevLex', linewidth=2.5, markersize=6, color='red')

        ax.set_yscale('log', nonpositive='clip')
        ax.set_xlabel('Number of Nodes (n)')
        ax.set_ylabel('Count (log scale)')
        # Place Kn(param) text inside the plot
        ax.text(0.25, 0.96, f'Kn({param})', transform=ax.transAxes, fontsize=12, fontweight='bold',
                verticalalignment='top', horizontalalignment='left',
                bbox=dict(boxstyle='round', facecolor='white', alpha=0.8))
        ax.legend(fontsize=11, loc='upper left')
        ax.grid(True, alpha=0.3)

        # Add ratio annotation in bottom-right corner
        if len(param_data) > 0:
            last_lex_count = param_data['Lex Count'].iloc[-1]
            last_revlex_count = param_data['RevLex Count'].iloc[-1]
            ratio = last_revlex_count / last_lex_count if last_lex_count > 0 else float('inf')
            ax.text(0.98, 0.02, f'RevLex/Lex Count Ratio: {ratio:.2f}x',
                    transform=ax.transAxes, fontsize=10, verticalalignment='bottom', horizontalalignment='right',
                    bbox=dict(boxstyle='round', facecolor='lightblue', alpha=0.8))

    # Remove empty subplots
    for j in range(num_params, len(axes)):
        fig.delaxes(axes[j])

    plt.tight_layout()
    plt.savefig('lex_revlex_count_comparison.png', dpi=300, bbox_inches='tight')
    plt.show()


# OptLex vs OptRevLex Count Comparison
def plot_optlex_optrevlex_count(df):
    """
    Plot count comparison between OptLex and OptRevLex
    """
    parameters = sorted(df['Parameter'].unique())
    num_params = len(parameters)
    cols = 2
    rows = math.ceil(num_params / cols)
    fig, axes = plt.subplots(rows, cols, figsize=(15, 6 * rows))
    axes = axes.flatten()
    #fig.suptitle('Count', fontsize=16, fontweight='bold')

    for i, param in enumerate(parameters):
        ax = axes[i]
        param_data = df[df['Parameter'] == param].sort_values('Graph_Size')
        if len(param_data) < 2:
            continue  # Skip if insufficient data points

        # OptLex vs OptRevLex Count comparison
        ax.plot(param_data['Graph_Size'], param_data['OptLex Count'],
                marker='o', label='OptLex', linewidth=2.5, markersize=6, color='green')
        ax.plot(param_data['Graph_Size'], param_data['OptRevLex Count'],
                marker='s', label='OptRevLex', linewidth=2.5, markersize=6, color='orange')

        ax.set_yscale('log', nonpositive='clip')
        ax.set_xlabel('Number of Nodes (n)')
        ax.set_ylabel('Count (log scale)')
        # Place Kn(param) text inside the plot
        ax.text(0.25, 0.96, f'Kn({param})', transform=ax.transAxes, fontsize=12, fontweight='bold',
                verticalalignment='top', horizontalalignment='left',
                bbox=dict(boxstyle='round', facecolor='white', alpha=0.8))
        ax.legend(fontsize=11, loc='upper left')
        ax.grid(True, alpha=0.3)

        # Add ratio annotation in bottom-right corner
        if len(param_data) > 0:
            last_optlex_count = param_data['OptLex Count'].iloc[-1]
            last_optrevlex_count = param_data['OptRevLex Count'].iloc[-1]
            ratio = last_optrevlex_count / last_optlex_count if last_optlex_count > 0 else float('inf')
            ax.text(0.98, 0.02, f'OptRevLex/OptLex Count Ratio: {ratio:.2f}x',
                    transform=ax.transAxes, fontsize=10, verticalalignment='bottom', horizontalalignment='right',
                    bbox=dict(boxstyle='round', facecolor='lightgreen', alpha=0.8))

    # Remove empty subplots
    for j in range(num_params, len(axes)):
        fig.delaxes(axes[j])

    plt.tight_layout()
    plt.savefig('optlex_optrevlex_count_comparison.png', dpi=300, bbox_inches='tight')
    plt.show()

def plot_lex_revlex_count_on_fixed_n12(df, fixed_n=12):
    """
    Plot count comparison between Lex and RevLex for a fixed number of nodes (n=12) across various graph degrees
    """
    param_data = df[(df['Graph_Size'] == fixed_n) & (df['Parameter'].isin(range(2, 10)))].sort_values('Parameter')
    if len(param_data) < 2:
        print(f"Insufficient data points for n={fixed_n}")
        return

    fig, ax = plt.subplots(figsize=(10, 6))
    fig.suptitle(f'Lex vs RevLex: Number of Generated Regular Graphs for K{fixed_n}', fontsize=16, fontweight='bold')

    # Lex vs RevLex Count comparison
    ax.plot(param_data['Parameter'], param_data['Lex Count'],
            marker='o', label='Lex', linewidth=2.5, markersize=6, color='blue')
    ax.plot(param_data['Parameter'], param_data['RevLex Count'],
            marker='s', label='RevLex', linewidth=2.5, markersize=6, color='red')

    ax.set_yscale('log', nonpositive='clip')
    ax.set_xlabel('Graph Degree (d)')
    ax.set_ylabel('Number of Generated Regular Graphs (log scale)')
    ax.legend(fontsize=11, loc='upper left')
    ax.grid(True, alpha=0.3)

    # Add ratio annotation in bottom-right corner
    if len(param_data) > 0:
        last_lex_count = param_data['Lex Count'].iloc[-1]
        last_revlex_count = param_data['RevLex Count'].iloc[-1]
        ratio = last_revlex_count / last_lex_count if last_lex_count > 0 else float('inf')
        ax.text(0.98, 0.02, f'RevLex/Lex Count Ratio: {ratio:.2f}x',
                transform=ax.transAxes, fontsize=10, verticalalignment='bottom', horizontalalignment='right',
                bbox=dict(boxstyle='round', facecolor='lightblue', alpha=0.8))

    plt.tight_layout()
    plt.savefig(f'lex_revlex_count_K{fixed_n}.png', dpi=300, bbox_inches='tight')
    plt.show()

def plot_lex_revlex_count_on_fixed_n(df):
    """
    Plot count comparison between Lex and RevLex for fixed number of nodes (n=12, 11, 9, 8) across various graph degrees
    in a 2x2 subplot grid
    """
    fixed_nodes = [12, 11, 9, 8]
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    axes = axes.flatten()
    fig.suptitle('Lex vs RevLex: Number of Generated Regular Graphs for Fixed n', fontsize=16, fontweight='bold')

    for i, n in enumerate(fixed_nodes):
        ax = axes[i]
        param_data = df[(df['Graph_Size'] == n) & (df['Parameter'].isin(range(2, 10)))].sort_values('Parameter')
        if len(param_data) < 2:
            print(f"Insufficient data points for n={n}")
            continue

        # Lex vs RevLex Count comparison
        ax.plot(param_data['Parameter'], param_data['Lex Count'],
                marker='o', label='Lex', linewidth=2.5, markersize=6, color='blue')
        ax.plot(param_data['Parameter'], param_data['RevLex Count'],
                marker='s', label='RevLex', linewidth=2.5, markersize=6, color='red')

        ax.set_yscale('log', nonpositive='clip')
        ax.set_xlabel('Graph Degree (d)')
        ax.set_ylabel('Number of Generated Regular Graphs (log scale)')
        # Place K_n(d) text inside the plot
        ax.text(0.02, 0.85, f'K{n}(d)', transform=ax.transAxes, fontsize=12, fontweight='bold',
                verticalalignment='top', horizontalalignment='left',
                bbox=dict(boxstyle='round', facecolor='white', alpha=0.8))
        ax.legend(fontsize=11, loc='upper left')
        ax.grid(True, alpha=0.3)

        # Add ratio annotation in bottom-right corner
        if len(param_data) > 0:
            last_lex_count = param_data['Lex Count'].iloc[-1]
            last_revlex_count = param_data['RevLex Count'].iloc[-1]
            ratio = last_revlex_count / last_lex_count if last_lex_count > 0 else float('inf')
            ax.text(0.98, 0.02, f'RevLex/Lex Count Ratio: {ratio:.2f}x',
                    transform=ax.transAxes, fontsize=10, verticalalignment='bottom', horizontalalignment='right',
                    bbox=dict(boxstyle='round', facecolor='lightblue', alpha=0.8))

    # Remove empty subplots (if any)
    for j in range(len(fixed_nodes), len(axes)):
        fig.delaxes(axes[j])

    plt.tight_layout()
    plt.savefig('lex_revlex_count_fixed_n.png', dpi=300, bbox_inches='tight')
    plt.show()

def plot_optlex_optrevlex_count_on_fixed_n(df):
    """
    Plot count comparison between OptLex and OptRevLex for fixed number of nodes (n=12, 11, 9, 8) across various graph degrees
    in a 2x2 subplot grid
    """
    fixed_nodes = [12, 11, 9, 8]
    fig, axes = plt.subplots(2, 2, figsize=(15, 12))
    axes = axes.flatten()
    fig.suptitle('OptLex vs OptRevLex: Number of Generated Regular Graphs for Fixed n', fontsize=16, fontweight='bold')

    for i, n in enumerate(fixed_nodes):
        ax = axes[i]
        param_data = df[(df['Graph_Size'] == n) & (df['Parameter'].isin(range(2, 10)))].sort_values('Parameter')
        if len(param_data) < 2:
            print(f"Insufficient data points for n={n}")
            continue

        # OptLex vs OptRevLex Count comparison
        ax.plot(param_data['Parameter'], param_data['OptLex Count'],
                marker='o', label='OptLex', linewidth=2.5, markersize=6, color='green')
        ax.plot(param_data['Parameter'], param_data['OptRevLex Count'],
                marker='s', label='OptRevLex', linewidth=2.5, markersize=6, color='orange')

        ax.set_yscale('log', nonpositive='clip')
        ax.set_xlabel('Graph Degree (d)')
        ax.set_ylabel('Number of Generated Regular Graphs (log scale)')
        # Place K_n(d) text inside the plot
        ax.text(0.02, 0.85, f'K{n}(d)', transform=ax.transAxes, fontsize=12, fontweight='bold',
                verticalalignment='top', horizontalalignment='left',
                bbox=dict(boxstyle='round', facecolor='white', alpha=0.8))
        ax.legend(fontsize=11, loc='upper left')
        ax.grid(True, alpha=0.3)

        # Add ratio annotation in bottom-right corner
        if len(param_data) > 0:
            last_optlex_count = param_data['OptLex Count'].iloc[-1]
            last_optrevlex_count = param_data['OptRevLex Count'].iloc[-1]
            ratio = last_optrevlex_count / last_optlex_count if last_optlex_count > 0 else float('inf')
            ax.text(0.98, 0.02, f'OptRevLex/OptLex Count Ratio: {ratio:.2f}x',
                    transform=ax.transAxes, fontsize=10, verticalalignment='bottom', horizontalalignment='right',
                    bbox=dict(boxstyle='round', facecolor='lightgreen', alpha=0.8))

    # Remove empty subplots (if any)
    for j in range(len(fixed_nodes), len(axes)):
        fig.delaxes(axes[j])

    plt.tight_layout()
    plt.savefig('optlex_optrevlex_count_fixed_n.png', dpi=300, bbox_inches='tight')
    plt.show()


# Updated Summary Statistics for Pairwise Comparison
def print_pairwise_summary_statistics(df):
    """
    Print summary statistics focused on pairwise comparisons
    """
    print("=" * 60)
    print("PAIRWISE COMPARISON SUMMARY STATISTICS")
    print("=" * 60)

    parameters = sorted(df['Parameter'].unique())

    for param in parameters:
        print(f"\n{'=' * 20} Kn({param}) {'=' * 20}")

        # Lex vs RevLex
        print("\nLEX vs REVLex:")
        param_data = df[df['Parameter'] == param]
        lex_cpu_max = param_data['Lex CPU'].max()
        revlex_cpu_max = param_data['RevLex CPU'].max()
        lex_count_max = param_data['Lex Count'].max()
        revlex_count_max = param_data['RevLex Count'].max()

        cpu_speedup = lex_cpu_max / revlex_cpu_max if revlex_cpu_max > 0 else float('inf')
        count_ratio = revlex_count_max / lex_count_max if lex_count_max > 0 else float('inf')

        print(f"  Max CPU Time - Lex: {lex_cpu_max:.2f}s, RevLex: {revlex_cpu_max:.2f}s (Speedup: {cpu_speedup:.2f}x)")
        print(f"  Max Count - Lex: {lex_count_max:.0f}, RevLex: {revlex_count_max:.0f} (Ratio: {count_ratio:.2f}x)")

        # OptLex vs OptRevLex
        print("\nOPTLEX vs OPTREVLex:")
        optlex_cpu_max = param_data['OptLex CPU'].max()
        optrevlex_cpu_max = param_data['OptRevLex CPU'].max()
        optlex_count_max = param_data['OptLex Count'].max()
        optrevlex_count_max = param_data['OptRevLex Count'].max()

        opt_cpu_speedup = optlex_cpu_max / optrevlex_cpu_max if optrevlex_cpu_max > 0 else float('inf')
        opt_count_ratio = optrevlex_count_max / optlex_count_max if optlex_count_max > 0 else float('inf')

        print(f"  Max CPU Time - OptLex: {optlex_cpu_max:.2f}s, OptRevLex: {optrevlex_cpu_max:.2f}s (Speedup: {opt_cpu_speedup:.2f}x)")
        print(f"  Max Count - OptLex: {optlex_count_max:.0f}, OptRevLex: {optrevlex_count_max:.0f} (Ratio: {opt_count_ratio:.2f}x)")


# Main execution
def main():
    # Specify the path to your Excel file
    excel_file_path = "Graph Generation Statistics.xlsx"  # Update this path if needed

    # Load and preprocess data
    df = load_and_preprocess_data(excel_file_path)

    if df is not None:
        print("Generating pairwise comparison plots...")

        # Lex vs RevLex plots
        print("1. Generating Lex vs RevLex comparisons...")
        #plot_lex_revlex_cpu(df)
       # plot_lex_revlex_count(df)

        # OptLex vs OptRevLex plots
        print("2. Generating OptLex vs OptRevLex comparisons...")
        #plot_optlex_optrevlex_cpu(df)
       # plot_optlex_optrevlex_count(df)

        # Lex vs RevLex plots on fixed n
        print("3. Generating Lex vs RevLex comparisons on fixed n...")
        plot_lex_revlex_count_on_fixed_n12(df)
        plot_lex_revlex_count_on_fixed_n(df)
        plot_optlex_optrevlex_count_on_fixed_n(df)
        # Print summary statistics
        print_pairwise_summary_statistics(df)

        print("All pairwise comparison plots generated successfully!")
        print("Plots saved as PNG files in the current directory:")
        print("  - lex_revlex_cpu_comparison.png")
        print("  - lex_revlex_count_comparison.png")
        print("  - optlex_optrevlex_cpu_comparison.png")
        print("  - optlex_optrevlex_count_comparison.png")
    else:
        print("Failed to load data. Please check the file path.")


if __name__ == "__main__":
    main()