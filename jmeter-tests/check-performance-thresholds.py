#!/usr/bin/env python3
"""
JMeter Performance Threshold Checker

This script analyzes JMeter results (JTL file) and checks if performance
thresholds are met. It's designed to be used in CI/CD pipelines.

Usage:
    python check-performance-thresholds.py <jtl_file>

Exit codes:
    0 - All thresholds passed
    1 - One or more thresholds failed
    2 - Error reading file or invalid format
"""

import sys
import csv
from collections import defaultdict


class PerformanceThresholds:
    """Define performance thresholds for the application"""
    MAX_AVG_RESPONSE_TIME = 500  # milliseconds
    MAX_95TH_PERCENTILE = 500    # milliseconds
    MAX_ERROR_RATE = 1.0         # percentage
    MIN_THROUGHPUT = 10          # requests per second


def parse_jtl_file(filename):
    """Parse JMeter JTL file and extract metrics"""
    samples = []
    
    try:
        with open(filename, 'r', encoding='utf-8') as f:
            # Skip the first line if it's XML header
            first_line = f.readline()
            if not first_line.startswith('<?xml'):
                f.seek(0)  # Reset to beginning if not XML
            
            reader = csv.DictReader(f)
            for row in reader:
                try:
                    sample = {
                        'timeStamp': int(row.get('timeStamp', 0)),
                        'elapsed': int(row.get('elapsed', 0)),
                        'label': row.get('label', ''),
                        'responseCode': row.get('responseCode', ''),
                        'success': row.get('success', 'true').lower() == 'true',
                        'bytes': int(row.get('bytes', 0)),
                        'sentBytes': int(row.get('sentBytes', 0)),
                        'latency': int(row.get('Latency', 0)),
                        'Connect': int(row.get('Connect', 0))
                    }
                    samples.append(sample)
                except (ValueError, KeyError) as e:
                    print(f"Warning: Skipping invalid row: {e}")
                    continue
    
    except FileNotFoundError:
        print(f"Error: File '{filename}' not found")
        sys.exit(2)
    except Exception as e:
        print(f"Error reading file: {e}")
        sys.exit(2)
    
    return samples


def calculate_metrics(samples):
    """Calculate performance metrics from samples"""
    if not samples:
        print("Error: No valid samples found in JTL file")
        sys.exit(2)
    
    # Calculate basic metrics
    total_samples = len(samples)
    failed_samples = sum(1 for s in samples if not s['success'])
    error_rate = (failed_samples / total_samples) * 100 if total_samples > 0 else 0
    
    # Calculate response times
    response_times = sorted([s['elapsed'] for s in samples])
    avg_response_time = sum(response_times) / len(response_times)
    min_response_time = min(response_times)
    max_response_time = max(response_times)
    
    # Calculate percentiles
    def percentile(data, p):
        index = int(len(data) * p / 100)
        return data[min(index, len(data) - 1)]
    
    p50 = percentile(response_times, 50)
    p90 = percentile(response_times, 90)
    p95 = percentile(response_times, 95)
    p99 = percentile(response_times, 99)
    
    # Calculate throughput
    if samples:
        time_span = (max(s['timeStamp'] for s in samples) - 
                    min(s['timeStamp'] for s in samples)) / 1000.0  # Convert to seconds
        throughput = total_samples / time_span if time_span > 0 else 0
    else:
        throughput = 0
    
    # Calculate metrics by label
    by_label = defaultdict(list)
    for sample in samples:
        by_label[sample['label']].append(sample)
    
    return {
        'total_samples': total_samples,
        'failed_samples': failed_samples,
        'error_rate': error_rate,
        'avg_response_time': avg_response_time,
        'min_response_time': min_response_time,
        'max_response_time': max_response_time,
        'p50': p50,
        'p90': p90,
        'p95': p95,
        'p99': p99,
        'throughput': throughput,
        'by_label': by_label
    }


def check_thresholds(metrics):
    """Check if metrics meet defined thresholds"""
    thresholds = PerformanceThresholds()
    passed = True
    
    print("\n" + "="*60)
    print("PERFORMANCE TEST RESULTS")
    print("="*60)
    
    print(f"\nTotal Samples: {metrics['total_samples']}")
    print(f"Failed Samples: {metrics['failed_samples']}")
    print(f"Error Rate: {metrics['error_rate']:.2f}%")
    
    print(f"\nResponse Times (ms):")
    print(f"  Average: {metrics['avg_response_time']:.2f}")
    print(f"  Min: {metrics['min_response_time']}")
    print(f"  Max: {metrics['max_response_time']}")
    print(f"  50th Percentile: {metrics['p50']}")
    print(f"  90th Percentile: {metrics['p90']}")
    print(f"  95th Percentile: {metrics['p95']}")
    print(f"  99th Percentile: {metrics['p99']}")
    
    print(f"\nThroughput: {metrics['throughput']:.2f} req/sec")
    
    print("\n" + "="*60)
    print("THRESHOLD CHECKS")
    print("="*60)
    
    # Check average response time
    if metrics['avg_response_time'] <= thresholds.MAX_AVG_RESPONSE_TIME:
        print(f"✓ Average Response Time: {metrics['avg_response_time']:.2f}ms <= {thresholds.MAX_AVG_RESPONSE_TIME}ms")
    else:
        print(f"✗ Average Response Time: {metrics['avg_response_time']:.2f}ms > {thresholds.MAX_AVG_RESPONSE_TIME}ms")
        passed = False
    
    # Check 95th percentile
    if metrics['p95'] <= thresholds.MAX_95TH_PERCENTILE:
        print(f"✓ 95th Percentile: {metrics['p95']}ms <= {thresholds.MAX_95TH_PERCENTILE}ms")
    else:
        print(f"✗ 95th Percentile: {metrics['p95']}ms > {thresholds.MAX_95TH_PERCENTILE}ms")
        passed = False
    
    # Check error rate
    if metrics['error_rate'] <= thresholds.MAX_ERROR_RATE:
        print(f"✓ Error Rate: {metrics['error_rate']:.2f}% <= {thresholds.MAX_ERROR_RATE}%")
    else:
        print(f"✗ Error Rate: {metrics['error_rate']:.2f}% > {thresholds.MAX_ERROR_RATE}%")
        passed = False
    
    # Check throughput
    if metrics['throughput'] >= thresholds.MIN_THROUGHPUT:
        print(f"✓ Throughput: {metrics['throughput']:.2f} req/sec >= {thresholds.MIN_THROUGHPUT} req/sec")
    else:
        print(f"✗ Throughput: {metrics['throughput']:.2f} req/sec < {thresholds.MIN_THROUGHPUT} req/sec")
        passed = False
    
    print("\n" + "="*60)
    if passed:
        print("RESULT: ALL THRESHOLDS PASSED ✓")
    else:
        print("RESULT: SOME THRESHOLDS FAILED ✗")
    print("="*60 + "\n")
    
    return passed


def main():
    if len(sys.argv) != 2:
        print("Usage: python check-performance-thresholds.py <jtl_file>")
        print("\nExample:")
        print("  python check-performance-thresholds.py results/auth-results.jtl")
        sys.exit(2)
    
    jtl_file = sys.argv[1]
    
    print(f"Analyzing JMeter results from: {jtl_file}")
    
    samples = parse_jtl_file(jtl_file)
    metrics = calculate_metrics(samples)
    passed = check_thresholds(metrics)
    
    sys.exit(0 if passed else 1)


if __name__ == "__main__":
    main()
